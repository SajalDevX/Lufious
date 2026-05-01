package ai.lufious.app.presentation.scan.viewmodel

import ai.lufious.app.core.utils.BaseViewModel
import ai.lufious.app.core.utils.DispatcherProvider
import ai.lufious.app.core.utils.Result
import ai.lufious.app.core.utils.Screen
import ai.lufious.app.core.utils.UiEffect
import ai.lufious.app.presentation.scan.data.usecases.GetScanHistoryUseCase
import ai.lufious.app.presentation.scan.data.usecases.ScanPlantUseCase
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val scanPlant: ScanPlantUseCase,
    private val getScanHistory: GetScanHistoryUseCase,
    dispatchers: DispatcherProvider
) : BaseViewModel<ScanEvent, ScanState>(ScanState(), dispatchers) {

    init {
        onEvent(ScanEvent.LoadHistory)
    }

    fun onEvent(event: ScanEvent) {
        viewModelScope.launch { handleEvent(event) }
    }

    override suspend fun handleEvent(event: ScanEvent) {
        when (event) {
            is ScanEvent.Scan -> {
                setState { copy(isScanning = true, error = null) }
                ioLaunch {
                    when (val result = scanPlant(event.imageBytes)) {
                        is Result.Success -> {
                            val scan = result.data ?: run {
                                setState { copy(isScanning = false, error = "Scan returned no data") }
                                return@ioLaunch
                            }
                            setState { copy(isScanning = false) }
                            emitEffect(UiEffect.Navigate(Screen.ScanResult.createRoute(scan.id)))
                        }
                        is Result.Error -> {
                            setState { copy(isScanning = false, error = result.message) }
                            emitEffect(UiEffect.ShowError(result.message ?: "Scan failed"))
                        }
                    }
                }
            }

            is ScanEvent.ScanFailed -> {
                setState { copy(isScanning = false, error = event.message) }
                emitEffect(UiEffect.ShowError(event.message))
            }

            ScanEvent.LoadHistory -> {
                setState { copy(isHistoryLoading = true) }
                ioLaunch {
                    when (val result = getScanHistory()) {
                        is Result.Success ->
                            setState { copy(scanHistory = result.data ?: emptyList(), isHistoryLoading = false) }
                        is Result.Error ->
                            setState { copy(isHistoryLoading = false) }
                    }
                }
            }
        }
    }
}
