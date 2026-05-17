package ai.lufious.app.presentation.home.viewmodel

import ai.lufious.app.core.network.dto.DailyForecastDto
import ai.lufious.app.presentation.garden.data.models.PlantModel

data class HomeState(
    val userName: String = "",
    val totalPlants: Int = 0,
    val plantsNeedingWater: List<PlantModel> = emptyList(),
    val recentPlants: List<PlantModel> = emptyList(),
    val weatherAlertsCount: Int = 0,
    val aiTip: String? = null,
    val currentTempC: Double? = null,
    val currentCondition: String? = null,
    val currentIcon: String? = null,
    val currentHumidity: Int? = null,
    val currentWindKph: Int? = null,
    val currentUvi: Double? = null,
    val weatherForecast: List<DailyForecastDto> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
