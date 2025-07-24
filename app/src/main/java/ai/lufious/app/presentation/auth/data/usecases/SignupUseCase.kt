package ai.lufious.app.presentation.auth.data.usecases

import ai.lufious.app.core.utils.Result
import ai.lufious.app.presentation.auth.data.models.UserModel
import ai.lufious.app.presentation.auth.data.repository.AuthRepository
import jakarta.inject.Inject

class SignupUseCase @Inject constructor(
    private val repo: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<UserModel> =
        try {
            repo.signupWithEmail(email, password)
        } catch (e: Exception) {
           Result.Error(message = e.message.toString())
        }
}

class SignupWithGoogleUseCase @Inject constructor(
    private val repo: AuthRepository
) {
    suspend operator fun invoke(token: String): Result<UserModel> = try {
        repo.signupWithGoogle(token)
    } catch (e: Exception) {
        Result.Error(message = e.message.toString())
    }
}