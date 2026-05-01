package ai.lufious.app.presentation.auth.data.usecases

import ai.lufious.app.core.utils.Result
import ai.lufious.app.presentation.auth.data.models.UserModel
import ai.lufious.app.presentation.auth.data.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repo: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<UserModel> =
        try {
            repo.loginWithEmail(email, password)
        } catch (e: Exception) {
            Result.Error(message = e.message.toString())
        }
}

class LoginWithGoogleUseCase @Inject constructor(
    private val repo: AuthRepository
) {
    suspend operator fun invoke(token: String): Result<UserModel> = try {
        repo.loginWithGoogle(token)
    } catch (e: Exception) {
        Result.Error(message = e.message.toString())
    }
}

class LoginWithFacebookUseCase @Inject constructor(
    private val repo: AuthRepository
) {
    suspend operator fun invoke(token: String): Result<UserModel> = try {
        repo.loginWithFacebook(token)
    } catch (e: Exception) {
        Result.Error(message = e.message.toString())
    }
}
