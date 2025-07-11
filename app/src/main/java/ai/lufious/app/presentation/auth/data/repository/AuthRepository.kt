package ai.lufious.app.presentation.auth.data.repository

interface AuthRepository {
    suspend fun signup(email: String, password: String): Result<User>
    suspend fun login(email: String, password: String): Result<User>
    fun signOut(): Result<Unit>
    val currentUser: User?
}
