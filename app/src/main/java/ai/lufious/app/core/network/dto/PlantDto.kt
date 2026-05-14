package ai.lufious.app.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlantDto(
    @SerialName("_id") val id: String,
    val userId: String,
    val nickname: String,
    val species: String,
    val photoUrl: String? = null,
    val locationTag: String = "Living Room",
    val wateringIntervalDays: Int = 7,
    val fertilizingIntervalDays: Int = 30,
    val lastWatered: Long = 0L,
    val lastFertilized: Long = 0L,
    val addedAt: Long = 0L,
    val healthStatus: String = "healthy"
)

@Serializable
data class PlantListResponse(val items: List<PlantDto>)

@Serializable
data class PlantCreateRequest(
    val nickname: String,
    val species: String,
    val photoUrl: String? = null,
    val locationTag: String = "Living Room",
    val wateringIntervalDays: Int = 7,
    val fertilizingIntervalDays: Int = 30
)

@Serializable
data class PlantPatchRequest(
    val nickname: String? = null,
    val species: String? = null,
    val photoUrl: String? = null,
    val locationTag: String? = null,
    val wateringIntervalDays: Int? = null,
    val fertilizingIntervalDays: Int? = null,
    val lastWatered: Long? = null,
    val lastFertilized: Long? = null,
    val healthStatus: String? = null
)

@Serializable
data class CareLogDto(
    @SerialName("_id") val id: String,
    val userId: String,
    val plantId: String,
    val type: String,
    val note: String = "",
    val timestamp: Long
)

@Serializable
data class CareLogListResponse(val items: List<CareLogDto>)

@Serializable
data class CareLogCreateRequest(
    val type: String,
    val note: String = ""
)
