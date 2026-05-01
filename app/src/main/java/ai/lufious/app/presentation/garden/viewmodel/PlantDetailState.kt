package ai.lufious.app.presentation.garden.viewmodel

import ai.lufious.app.presentation.garden.data.models.CareLogModel
import ai.lufious.app.presentation.garden.data.models.PlantModel

data class PlantDetailState(
    val plant: PlantModel? = null,
    val careLogs: List<CareLogModel> = emptyList(),
    val isLoading: Boolean = false,
    val showLogDialog: Boolean = false,
    val selectedLogType: String = "watered",
    val logNote: String = ""
)
