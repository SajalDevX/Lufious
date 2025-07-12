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
}
