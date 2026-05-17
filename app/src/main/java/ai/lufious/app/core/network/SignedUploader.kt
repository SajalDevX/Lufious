package ai.lufious.app.core.network

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

@Singleton
class SignedUploader @Inject constructor(
    private val httpClient: OkHttpClient
) {
    /**
     * PUT bytes directly to a Firebase Storage signed URL minted by the backend.
     * Returns the public/long-lived downloadUrl supplied by the backend.
     */
    suspend fun upload(
        uploadUrl: String,
        bytes: ByteArray,
        contentType: String = "image/jpeg"
    ) {
        val media = contentType.toMediaTypeOrNull()
        val body = bytes.toRequestBody(media)
        val req = Request.Builder()
            .url(uploadUrl)
            .put(body)
            .build()
        httpClient.newCall(req).execute().use { res ->
            if (!res.isSuccessful) {
                val errBody = runCatching { res.body?.string()?.take(1500) }.getOrNull()
                Log.e("SignedUploader", "S3 PUT failed ${res.code} ${res.message} body=$errBody")
                error("upload failed: ${res.code} ${res.message}")
            }
        }
    }
}
