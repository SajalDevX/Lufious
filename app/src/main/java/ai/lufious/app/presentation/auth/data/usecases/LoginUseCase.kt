package ai.lufious.app.presentation.auth.data.usecases

import ai.lufious.app.core.utils.Result
import ai.lufious.app.presentation.auth.data.repository.AuthRepository
import jakarta.inject.Inject

class LoginUseCase @Inject constructor(
    private val repo: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> =
        try {
            Result.Success(repo.login(email, password))
        } catch(e: Exception) {
            Result.Error(message = e.message.toString())
        }
}