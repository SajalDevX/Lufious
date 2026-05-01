package ai.lufious.app.presentation.scan.data.datasource

import ai.lufious.app.core.firebase.utils.ScanFields
import ai.lufious.app.core.local_cache.LocalCacheManager
import ai.lufious.app.presentation.scan.data.models.ScanResultModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

class ScanDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val localCache: LocalCacheManager
) {
    private val uid get() = localCache.getUser()?.uid ?: error("User not logged in")

    private fun scansRef() =
        firestore.collection("users").document(uid).collection(ScanFields.COLLECTION)

    /**
     * Sends image bytes to the AI plant identification service.
     * TODO: Replace mock with real Plant.id API or backend endpoint.
     */
    suspend fun scanPlant(@Suppress("UNUSED_PARAMETER") imageBytes: ByteArray): ScanResultModel {
        delay(1500L) // simulate AI processing time
        return ScanResultModel(
            speciesName = "Monstera Deliciosa",
            commonName = "Swiss Cheese Plant",
            confidence = 0.97f,
            healthStatus = "healthy",
            diagnosis = "Plant appears healthy. Leaves show rich green coloring with no visible signs of disease or pest damage.",
            carePlan = "Water every 7–10 days when the top inch of soil is dry. Keep in bright indirect light. Mist leaves occasionally to maintain humidity.",
            timestamp = System.currentTimeMillis()
        )
    }

    suspend fun saveScan(scan: ScanResultModel): ScanResultModel {
        val doc = scansRef().document()
        val withId = scan.copy(id = doc.id)
        doc.set(withId.toMap()).await()
        return withId
    }

    suspend fun getScanHistory(): List<ScanResultModel> =
        scansRef()
            .orderBy(ScanFields.TIMESTAMP, Query.Direction.DESCENDING)
            .limit(20)
            .get().await()
            .documents
            .mapNotNull { it.toScanResultModel() }

    suspend fun getScanById(scanId: String): ScanResultModel =
        scansRef().document(scanId).get().await().toScanResultModel()
            ?: error("Scan $scanId not found")

    private fun ScanResultModel.toMap(): Map<String, Any?> = mapOf(
        ScanFields.SPECIES_NAME to speciesName,
        ScanFields.COMMON_NAME to commonName,
        ScanFields.CONFIDENCE to confidence,
        ScanFields.HEALTH_STATUS to healthStatus,
        ScanFields.DIAGNOSIS to diagnosis,
        ScanFields.CARE_PLAN to carePlan,
        ScanFields.TIMESTAMP to timestamp
    )

    private fun DocumentSnapshot.toScanResultModel(): ScanResultModel? = try {
        ScanResultModel(
            id = id,
            speciesName = getString(ScanFields.SPECIES_NAME) ?: "",
            commonName = getString(ScanFields.COMMON_NAME) ?: "",
            confidence = (getDouble(ScanFields.CONFIDENCE) ?: 0.0).toFloat(),
            healthStatus = getString(ScanFields.HEALTH_STATUS) ?: "healthy",
            diagnosis = getString(ScanFields.DIAGNOSIS) ?: "",
            carePlan = getString(ScanFields.CARE_PLAN) ?: "",
            timestamp = getLong(ScanFields.TIMESTAMP) ?: 0L
        )
    } catch (e: Exception) { null }
}
