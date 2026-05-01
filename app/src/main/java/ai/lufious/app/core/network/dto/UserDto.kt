package ai.lufious.app.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    @SerialName("_id") val id: String,
    val email: String,
    val name: String? = null,
    val displayName: String? = null,
    val photoUrl: String? = null,
    val phone: String? = null,
    val provider: String = "email",
    val lat: Double? = null,
    val lon: Double? = null,
    val timezone: String? = null,
    val locale: String = "en",
    val experienceLevel: String = "beginner",
    val wateringReminderTime: String = "09:00",
    val followedCategories: List<String> = emptyList(),
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)

@Serializable
data class HomeDashboardDto(
    val user: UserDto? = null,
    val totalPlants: Int = 0,
    val needsWater: List<PlantDto> = emptyList(),
    val recentPlants: List<PlantDto> = emptyList(),
    val weather: WeatherDto? = null,
    val aiTip: AiTipDto? = null,
    val weatherAlertsCount: Int = 0
)

@Serializable
data class WeatherDto(
    val temp: Double? = null,
    val description: String? = null,
    val icon: String? = null
)

@Serializable
data class AiTipDto(
    val content: String
)
