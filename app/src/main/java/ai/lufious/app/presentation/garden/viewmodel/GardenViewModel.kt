package ai.lufious.app.presentation.garden.viewmodel

import ai.lufious.app.core.utils.BaseViewModel
import ai.lufious.app.core.utils.DispatcherProvider
import ai.lufious.app.core.utils.Result
import ai.lufious.app.presentation.garden.data.usecases.GetPlantsUseCase
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GardenViewModel @Inject constructor(
    private val getPlants: GetPlantsUseCase,
    dispatchers: DispatcherProvider
) : BaseViewModel<GardenEvent, GardenState>(GardenState(), dispatchers) {

    init {
        onEvent(GardenEvent.LoadPlants)
    }

    fun onEvent(event: GardenEvent) {
        viewModelScope.launch { handleEvent(event) }
    }

    override suspend fun handleEvent(event: GardenEvent) {
        when (event) {
            GardenEvent.LoadPlants -> {
                setState { copy(isLoading = true, error = null) }
                ioLaunch {
                    when (val result = getPlants()) {
                        is Result.Success ->
                            setState { copy(plants = result.data ?: emptyList(), isLoading = false) }
                        is Result.Error ->
                            setState { copy(isLoading = false, error = result.message) }
                    }
                }
            }
        }
    }
}
