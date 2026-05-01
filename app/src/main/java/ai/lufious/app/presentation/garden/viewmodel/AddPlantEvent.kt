package ai.lufious.app.presentation.garden.viewmodel

sealed class AddPlantEvent {
    data class NicknameChanged(val value: String) : AddPlantEvent()
    data class SpeciesChanged(val value: String) : AddPlantEvent()
    data class LocationChanged(val value: String) : AddPlantEvent()
    data class WateringIntervalChanged(val value: String) : AddPlantEvent()
    object Submit : AddPlantEvent()
}
