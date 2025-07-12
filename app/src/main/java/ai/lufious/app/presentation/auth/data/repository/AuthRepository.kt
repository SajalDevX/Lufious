package ai.lufious.app.presentation.auth.data.repository

import ai.lufious.app.presentation.auth.data.models.UserModel
import ai.lufious.app.core.utils.Result

interface AuthRepository {
    suspend fun loginWithEmail(email: String, password: String): Result<UserModel>
    suspend fun signupWithEmail(email: String, password: String): Result<UserModel>
    suspend fun loginWithGoogle(idToken: String): Result<UserModel>
    suspend fun loginWithFacebook(accessToken: String): Result<UserModel>
    fun signOut(): Result<Unit>
    val currentUser: UserModel?
}
