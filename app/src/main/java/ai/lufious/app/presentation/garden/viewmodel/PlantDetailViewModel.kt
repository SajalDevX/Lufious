package ai.lufious.app.presentation.garden.viewmodel

import ai.lufious.app.core.utils.BaseViewModel
import ai.lufious.app.core.utils.DispatcherProvider
import ai.lufious.app.core.utils.Result
import ai.lufious.app.core.utils.UiEffect
import ai.lufious.app.presentation.garden.data.usecases.GetCareLogsUseCase
import ai.lufious.app.presentation.garden.data.usecases.GetPlantByIdUseCase
import ai.lufious.app.presentation.garden.data.usecases.LogCareActionUseCase
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlantDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getPlantById: GetPlantByIdUseCase,
    private val logCareAction: LogCareActionUseCase,
    private val getCareLogs: GetCareLogsUseCase,
    dispatchers: DispatcherProvider
) : BaseViewModel<PlantDetailEvent, PlantDetailState>(PlantDetailState(), dispatchers) {

    private val plantId: String = savedStateHandle["plantId"] ?: ""

    init {
        loadPlant()
    }

    fun onEvent(event: PlantDetailEvent) {
        viewModelScope.launch { handleEvent(event) }
    }

    override suspend fun handleEvent(event: PlantDetailEvent) {
        when (event) {
            is PlantDetailEvent.LogTypeSelected ->
                setState { copy(selectedLogType = event.type) }
            is PlantDetailEvent.NoteChanged ->
                setState { copy(logNote = event.note) }
            PlantDetailEvent.ShowLogDialog ->
                setState { copy(showLogDialog = true) }
            PlantDetailEvent.DismissLogDialog ->
                setState { copy(showLogDialog = false, logNote = "", selectedLogType = "watered") }
            PlantDetailEvent.SubmitLog -> {
                setState { copy(isLoading = true) }
                ioLaunch {
                    when (val result = logCareAction(
                        plantId,
                        state.value.selectedLogType,
                        state.value.logNote
                    )) {
                        is Result.Success -> {
                            val log = result.data ?: run {
                                setState { copy(isLoading = false) }
                                return@ioLaunch
                            }
                            val updatedLogs = listOf(log) + state.value.careLogs
                            setState {
                                copy(
                                    careLogs = updatedLogs,
                                    isLoading = false,
                                    showLogDialog = false,
                                    logNote = "",
                                    selectedLogType = "watered"
                                )
                            }
                        }
                        is Result.Error -> {
                            setState { copy(isLoading = false) }
                            emitEffect(UiEffect.ShowError(result.message ?: "Failed to log action"))
                        }
                    }
                }
            }
        }
    }

    private fun loadPlant() {
        setState { copy(isLoading = true) }
        ioLaunch {
            when (val plantResult = getPlantById(plantId)) {
                is Result.Success -> setState { copy(plant = plantResult.data, isLoading = false) }
                is Result.Error -> setState { copy(isLoading = false) }
            }
            when (val logsResult = getCareLogs(plantId)) {
                is Result.Success -> setState { copy(careLogs = logsResult.data ?: emptyList()) }
                is Result.Error -> {}
            }
        }
    }
}
