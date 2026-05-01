package ai.lufious.app.presentation.garden.data.datasource

import ai.lufious.app.core.firebase.utils.LogFields
import ai.lufious.app.core.firebase.utils.PlantFields
import ai.lufious.app.core.local_cache.LocalCacheManager
import ai.lufious.app.presentation.garden.data.models.CareLogModel
import ai.lufious.app.presentation.garden.data.models.PlantModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class PlantDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val localCache: LocalCacheManager
) {
    private val uid get() = localCache.getUser()?.uid ?: error("User not logged in")

    private fun plantsRef() =
        firestore.collection("users").document(uid).collection(PlantFields.COLLECTION)

    private fun logsRef(plantId: String) =
        plantsRef().document(plantId).collection(LogFields.COLLECTION)

    suspend fun addPlant(plant: PlantModel): PlantModel {
        val doc = plantsRef().document()
        val withId = plant.copy(id = doc.id, addedAt = System.currentTimeMillis())
        doc.set(withId.toMap()).await()
        return withId
    }

    suspend fun getPlants(): List<PlantModel> =
        plantsRef()
            .orderBy(PlantFields.ADDED_AT, Query.Direction.DESCENDING)
            .get().await()
            .documents
            .mapNotNull { it.toPlantModel() }

    suspend fun getPlantById(plantId: String): PlantModel =
        plantsRef().document(plantId).get().await().toPlantModel()
            ?: error("Plant $plantId not found")

    suspend fun addCareLog(plantId: String, log: CareLogModel): CareLogModel {
        val doc = logsRef(plantId).document()
        val withId = log.copy(id = doc.id, timestamp = System.currentTimeMillis())
        doc.set(withId.toMap()).await()
        return withId
    }

    suspend fun getCareLogs(plantId: String): List<CareLogModel> =
        logsRef(plantId)
            .orderBy(LogFields.TIMESTAMP, Query.Direction.DESCENDING)
            .get().await()
            .documents
            .mapNotNull { it.toCareLogModel() }

    private fun PlantModel.toMap(): Map<String, Any?> = mapOf(
        PlantFields.NICKNAME to nickname,
        PlantFields.SPECIES to species,
        PlantFields.PHOTO_URL to photoUrl,
        PlantFields.LOCATION_TAG to locationTag,
        PlantFields.WATERING_INTERVAL_DAYS to wateringIntervalDays,
        PlantFields.FERTILIZING_INTERVAL_DAYS to fertilizingIntervalDays,
        PlantFields.LAST_WATERED to lastWatered,
        PlantFields.LAST_FERTILIZED to lastFertilized,
        PlantFields.ADDED_AT to addedAt,
        PlantFields.HEALTH_STATUS to healthStatus
    )

    private fun CareLogModel.toMap(): Map<String, Any?> = mapOf(
        LogFields.TYPE to type,
        LogFields.NOTE to note,
        LogFields.TIMESTAMP to timestamp
    )

    private fun DocumentSnapshot.toPlantModel(): PlantModel? = try {
        PlantModel(
            id = id,
            nickname = getString(PlantFields.NICKNAME) ?: "",
            species = getString(PlantFields.SPECIES) ?: "",
            photoUrl = getString(PlantFields.PHOTO_URL) ?: "",
            locationTag = getString(PlantFields.LOCATION_TAG) ?: "Living Room",
            wateringIntervalDays = getLong(PlantFields.WATERING_INTERVAL_DAYS)?.toInt() ?: 7,
            fertilizingIntervalDays = getLong(PlantFields.FERTILIZING_INTERVAL_DAYS)?.toInt() ?: 30,
            lastWatered = getLong(PlantFields.LAST_WATERED) ?: 0L,
            lastFertilized = getLong(PlantFields.LAST_FERTILIZED) ?: 0L,
            addedAt = getLong(PlantFields.ADDED_AT) ?: 0L,
            healthStatus = getString(PlantFields.HEALTH_STATUS) ?: "healthy"
        )
    } catch (e: Exception) { null }

    private fun DocumentSnapshot.toCareLogModel(): CareLogModel? = try {
        CareLogModel(
            id = id,
            type = getString(LogFields.TYPE) ?: "",
            note = getString(LogFields.NOTE) ?: "",
            timestamp = getLong(LogFields.TIMESTAMP) ?: 0L
        )
    } catch (e: Exception) { null }
}
