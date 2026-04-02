package ai.lufious.app.presentation.garden.viewmodel

import ai.lufious.app.core.utils.BaseViewModel
import ai.lufious.app.core.utils.DispatcherProvider
import ai.lufious.app.core.utils.Result
import ai.lufious.app.core.utils.UiEffect
import ai.lufious.app.presentation.garden.data.usecases.AddPlantUseCase
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddPlantViewModel @Inject constructor(
    private val addPlant: AddPlantUseCase,
    dispatchers: DispatcherProvider
) : BaseViewModel<AddPlantEvent, AddPlantState>(AddPlantState(), dispatchers) {

    fun onEvent(event: AddPlantEvent) {
        viewModelScope.launch { handleEvent(event) }
    }

    override suspend fun handleEvent(event: AddPlantEvent) {
        when (event) {
            is AddPlantEvent.NicknameChanged -> {
                val canSubmit = event.value.isNotBlank() &&
                    state.value.species.isNotBlank() &&
                    (state.value.wateringIntervalDays.toIntOrNull() ?: 0) > 0
                setState { copy(nickname = event.value, isSubmitEnabled = canSubmit) }
            }
            is AddPlantEvent.SpeciesChanged -> {
                val canSubmit = state.value.nickname.isNotBlank() &&
                    event.value.isNotBlank() &&
                    (state.value.wateringIntervalDays.toIntOrNull() ?: 0) > 0
                setState { copy(species = event.value, isSubmitEnabled = canSubmit) }
            }
            is AddPlantEvent.LocationChanged ->
                setState { copy(locationTag = event.value) }
            is AddPlantEvent.WateringIntervalChanged -> {
                val canSubmit = state.value.nickname.isNotBlank() &&
                    state.value.species.isNotBlank() &&
                    (event.value.toIntOrNull() ?: 0) > 0
                setState { copy(wateringIntervalDays = event.value, isSubmitEnabled = canSubmit) }
            }
            AddPlantEvent.Submit -> {
                val days = state.value.wateringIntervalDays.toIntOrNull() ?: return
                setState { copy(isLoading = true) }
                ioLaunch {
                    when (val result = addPlant(
                        state.value.nickname,
                        state.value.species,
                        state.value.locationTag,
                        days
                    )) {
                        is Result.Success -> emitEffect(UiEffect.Navigate("back"))
                        is Result.Error -> {
                            setState { copy(isLoading = false) }
                            emitEffect(UiEffect.ShowError(result.message ?: "Failed to add plant"))
                        }
                    }
                }
            }
        }
    }
}
