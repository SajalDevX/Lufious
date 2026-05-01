package ai.lufious.app.presentation.garden.viewmodel

data class AddPlantState(
    val nickname: String = "",
    val species: String = "",
    val locationTag: String = "Living Room",
    val wateringIntervalDays: String = "7",
    val isLoading: Boolean = false,
    val isSubmitEnabled: Boolean = false
)
