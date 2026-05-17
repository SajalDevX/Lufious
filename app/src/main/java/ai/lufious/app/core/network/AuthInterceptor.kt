package ai.lufious.app.core.network

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import javax.inject.Singleton
import okhttp3.Interceptor
import okhttp3.Response

@Singleton
class AuthInterceptor @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        // Skip third-party hosts (e.g. S3 presigned PUT). They use query-string
        // signatures and reject any extra Authorization header.
        val host = original.url.host
        val isBackend = host.endsWith("lufious.ai") ||
            host == "65.1.178.39" ||
            host == "35.154.133.88" ||
            host == "43.205.12.74" ||
            host == "localhost" ||
            host == "10.0.2.2"
        if (!isBackend) return chain.proceed(original)

        if (original.header("Authorization") != null) return chain.proceed(original)

        val firstToken = currentIdToken(forceRefresh = false)
        val firstResponse = chain.proceed(original.withBearer(firstToken))
        if (firstResponse.code != 401 || firstToken == null) return firstResponse

        firstResponse.close()
        val refreshed = currentIdToken(forceRefresh = true) ?: return chain.proceed(original)
        return chain.proceed(original.withBearer(refreshed))
    }

    private fun currentIdToken(forceRefresh: Boolean): String? {
        val user = firebaseAuth.currentUser ?: return null
        return runCatching {
            Tasks.await(user.getIdToken(forceRefresh)).token
        }.getOrNull()
    }

    private fun okhttp3.Request.withBearer(token: String?): okhttp3.Request {
        if (token == null) return this
        return newBuilder().header("Authorization", "Bearer $token").build()
    }
}
