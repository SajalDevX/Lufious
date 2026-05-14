package ai.lufious.app.presentation.home.viewmodel

import ai.lufious.app.presentation.garden.data.models.PlantModel

data class HomeState(
    val userName: String = "",
    val totalPlants: Int = 0,
    val plantsNeedingWater: List<PlantModel> = emptyList(),
    val recentPlants: List<PlantModel> = emptyList(),
    val weatherAlertsCount: Int = 0,
    val aiTip: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
