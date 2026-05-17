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
data class WeatherAlertDto(
    val event: String,
    val description: String,
    val start: Long,
    val end: Long
)

@Serializable
data class DailyForecastDto(
    val dt: Long,
    val tempMin: Double? = null,
    val tempMax: Double? = null,
    val description: String? = null,
    val icon: String? = null
)

@Serializable
data class WeatherDto(
    val temp: Double? = null,
    val description: String? = null,
    val icon: String? = null,
    val humidity: Int? = null,
    val windKph: Int? = null,
    val uvi: Double? = null,
    val daily: List<DailyForecastDto> = emptyList(),
    val alerts: List<WeatherAlertDto> = emptyList(),
    val fetchedAt: Long = 0L
)

@Serializable
data class LocationPatchRequest(
    val lat: Double,
    val lon: Double,
    val timezone: String? = null
)

@Serializable
data class NotificationPrefsDto(
    @SerialName("_id") val id: String? = null,
    val watering: Boolean = true,
    val scanReady: Boolean = true,
    val newListing: Boolean = true,
    val wishlist: Boolean = true,
    val quietHoursStart: String? = null,
    val quietHoursEnd: String? = null
)

@Serializable
data class NotificationPrefsPatchRequest(
    val watering: Boolean? = null,
    val scanReady: Boolean? = null,
    val newListing: Boolean? = null,
    val wishlist: Boolean? = null,
    val quietHoursStart: String? = null,
    val quietHoursEnd: String? = null
)

@Serializable
data class FcmTokenRequest(val token: String)

@Serializable
data class ProfilePatchRequest(
    val displayName: String? = null,
    val name: String? = null,
    val photoUrl: String? = null,
    val phone: String? = null,
    val experienceLevel: String? = null,
    val wateringReminderTime: String? = null,
    val followedCategories: List<String>? = null
)

@Serializable
data class AiTipDto(
    val content: String
)
