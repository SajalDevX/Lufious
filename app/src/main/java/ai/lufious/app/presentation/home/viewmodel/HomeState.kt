package ai.lufious.app.presentation.home.viewmodel

import ai.lufious.app.presentation.garden.data.models.PlantModel

data class HomeState(
    val userName: String = "",
    val totalPlants: Int = 0,
    val plantsNeedingWater: List<PlantModel> = emptyList(),
    val isLoading: Boolean = false
)
