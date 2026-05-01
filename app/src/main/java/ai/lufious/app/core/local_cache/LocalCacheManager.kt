package ai.lufious.app.core.local_cache

import ai.lufious.app.presentation.auth.data.models.UserModel

interface LocalCacheManager {
    fun saveUser(user: UserModel)
    fun getUser(): UserModel?
    fun clearUser()

    fun saveAuthToken(token: String)
    fun getAuthToken(): String?

    fun clearAll()

    fun isPostOnboardingComplete(): Boolean
    fun setPostOnboardingComplete()
}
