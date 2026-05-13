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

    fun getOnboardingStep(): Int
    fun setOnboardingStep(step: Int)

    fun setGardenerLevel(level: String)
    fun getGardenerLevel(): String?

    fun setInterestCategories(categories: Set<String>)
    fun getInterestCategories(): Set<String>

    fun setGardenerGoal(goal: String)
    fun getGardenerGoal(): String?

    fun setClimateZone(zone: String)
    fun getClimateZone(): String?

    fun setLivingSpace(space: String)
    fun getLivingSpace(): String?
}
