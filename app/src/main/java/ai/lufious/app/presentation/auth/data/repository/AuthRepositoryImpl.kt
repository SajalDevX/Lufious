package ai.lufious.app.presentation.auth.data.repository

import ai.lufious.app.presentation.auth.data.datasource.FirebaseAuthDataSource
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject
import ai.lufious.app.core.utils.Result
import ai.lufious.app.presentation.auth.data.models.UserModel

class AuthRepositoryImpl @Inject constructor(
    private val ds: FirebaseAuthDataSource
) : AuthRepository {

    override suspend fun loginWithGoogle(idToken: String): Result<UserModel> = wrap<UserModel> {
        ds.loginWithGoogle(idToken).toDomain()
    }
    override suspend fun signupWithGoogle(idToken: String): Result<UserModel> = wrap<UserModel> {
        ds.signupWithGoogle(idToken).toDomain()
    }

    override suspend fun loginWithFacebook(accessToken: String): Result<UserModel> = wrap<UserModel> {
        ds.loginWithFacebook(accessToken).toDomain()
    }

    override suspend fun loginWithEmail(email: String, password: String): Result<UserModel> = wrap<UserModel> {
        ds.loginWithEmail(email, password).toDomain()
    }

    override suspend fun signupWithEmail(email: String, password: String): Result<UserModel> = wrap<UserModel> {
        ds.signupWithEmail(email, password).toDomain()
    }
    override fun signOut(): Result<Unit> =
        try {
            ds.signOut()
            Result.Success(Unit)
        } catch (e: Throwable) {
            Result.Error(e.message.orEmpty())
        }

    override val currentUser: UserModel?
        get() = ds.currentUser?.toDomain()

    private suspend fun <T> wrap(block: suspend () -> T): Result<T> =
        try {
            Result.Success(block())
        } catch (e: Throwable) {
            Result.Error(e.message.toString())
        }

    private fun FirebaseUser.toDomain() = UserModel(
        uid = uid,
        email = email.orEmpty(),
        displayName = displayName,
        phoneNumber = phoneNumber,
        photoUrl = photoUrl?.toString(),
        creationTimestamp = metadata?.creationTimestamp,
        lastSignInTimestamp = metadata?.lastSignInTimestamp
    )
}
