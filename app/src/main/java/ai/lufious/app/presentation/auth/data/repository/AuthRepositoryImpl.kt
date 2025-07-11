package ai.lufious.app.presentation.auth.data.repository

import ai.lufious.app.presentation.auth.data.datasource.FirebaseAuthDataSource
import jakarta.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val dataSource: FirebaseAuthDataSource
): AuthRepository {
    override suspend fun signup(email: String, password: String): Result<User> =
        runCatching {
            val fbUser = dataSource.signup(email, password)
            User(uid = fbUser.uid, email = fbUser.email)
        }

    override suspend fun login(email: String, password: String): Result<User> =
        runCatching {
            val fbUser = dataSource.login(email, password)
            User(uid = fbUser.uid, email = fbUser.email)
        }

    override fun signOut(): Result<Unit> {
        TODO("Not yet implemented")
    }

    override val currentUser: User?
        get() = dataSource.currentUser?.let { User(it.uid, it.email) }
}
