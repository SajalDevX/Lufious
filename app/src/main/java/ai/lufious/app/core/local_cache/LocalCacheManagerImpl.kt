package ai.lufious.app.core.local_cache

import ai.lufious.app.presentation.auth.data.models.UserModel
import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit
import com.google.gson.Gson

@Singleton
class LocalCacheManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : LocalCacheManager {

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences("app_cache", Context.MODE_PRIVATE)
    }

    private val gson = Gson()

    override fun saveUser(user: UserModel) {
        val json = gson.toJson(user)
        prefs.edit { putString("user", json) }
    }



    override fun getUser(): UserModel? {
        val json = prefs.getString("user", null) ?: return null
        return gson.fromJson(json, UserModel::class.java)
    }

    override fun clearUser() {
        prefs.edit { remove("user") }
    }

    override fun saveAuthToken(token: String) {
        prefs.edit { putString("auth_token", token) }
    }

    override fun getAuthToken(): String? {
        return prefs.getString("auth_token", null)
    }

    override fun clearAll() {
        prefs.edit { clear() }
    }

    override fun isPostOnboardingComplete(): Boolean =
        prefs.getBoolean("post_onboarding_complete", false)

    override fun setPostOnboardingComplete() {
        prefs.edit { putBoolean("post_onboarding_complete", true) }
    }

    override fun getOnboardingStep(): Int = prefs.getInt("onboarding_step", 0)
    override fun setOnboardingStep(step: Int) {
        prefs.edit { putInt("onboarding_step", step) }
    }

    override fun setGardenerLevel(level: String) {
        prefs.edit { putString("gardener_level", level) }
    }
    override fun getGardenerLevel(): String? = prefs.getString("gardener_level", null)

    override fun setInterestCategories(categories: Set<String>) {
        prefs.edit { putStringSet("interest_categories", categories) }
    }
    override fun getInterestCategories(): Set<String> =
        prefs.getStringSet("interest_categories", emptySet()) ?: emptySet()

    override fun setGardenerGoal(goal: String) {
        prefs.edit { putString("gardener_goal", goal) }
    }
    override fun getGardenerGoal(): String? = prefs.getString("gardener_goal", null)

    override fun setClimateZone(zone: String) {
        prefs.edit { putString("climate_zone", zone) }
    }
    override fun getClimateZone(): String? = prefs.getString("climate_zone", null)

    override fun setLivingSpace(space: String) {
        prefs.edit { putString("living_space", space) }
    }
    override fun getLivingSpace(): String? = prefs.getString("living_space", null)
}
