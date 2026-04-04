package ai.lufious.app.core.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.coroutines.tasks.await
import ai.lufious.app.presentation.auth.data.models.UserModel
import ai.lufious.app.core.firebase.utils.PlantFields

class PlantCareWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val prefs = context.getSharedPreferences("app_cache", Context.MODE_PRIVATE)
        val userJson = prefs.getString("user", null) ?: return Result.success()
        val user = runCatching { Gson().fromJson(userJson, UserModel::class.java) }.getOrNull()
            ?: return Result.success()

        val uid = user.uid.ifBlank { return Result.success() }

        return try {
            val now = System.currentTimeMillis()
            val snapshot = FirebaseFirestore.getInstance()
                .collection("users").document(uid)
                .collection("plants")
                .get()
                .await()

            val plantsNeedingWater = snapshot.documents.mapNotNull { doc ->
                val nickname = doc.getString(PlantFields.NICKNAME) ?: return@mapNotNull null
                val intervalDays = (doc.getLong(PlantFields.WATERING_INTERVAL_DAYS) ?: 0L).toInt()
                val lastWatered = doc.getLong(PlantFields.LAST_WATERED) ?: 0L
                if (intervalDays > 0 && (now - lastWatered) >= intervalDays * 86_400_000L) nickname
                else null
            }

            if (plantsNeedingWater.isNotEmpty()) {
                val message = when {
                    plantsNeedingWater.size == 1 -> "Time to water your ${plantsNeedingWater[0]}!"
                    plantsNeedingWater.size <= 3 ->
                        "Time to water: ${plantsNeedingWater.joinToString(", ")}"
                    else ->
                        "Time to water ${plantsNeedingWater.size} plants in your garden!"
                }
                NotificationHelper.showCareReminder(context, message)
            }

            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }
}
