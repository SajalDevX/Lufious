package ai.lufious.app.presentation.garden.viewmodel

import ai.lufious.app.presentation.garden.data.models.PlantModel

data class GardenState(
    val plants: List<PlantModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
