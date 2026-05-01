package ai.lufious.app.presentation.scan.viewmodel

import ai.lufious.app.core.utils.BaseViewModel
import ai.lufious.app.core.utils.DispatcherProvider
import ai.lufious.app.core.utils.Result
import ai.lufious.app.presentation.scan.data.usecases.GetScanByIdUseCase
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanResultViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getScanById: GetScanByIdUseCase,
    dispatchers: DispatcherProvider
) : BaseViewModel<ScanResultEvent, ScanResultState>(ScanResultState(), dispatchers) {

    private val scanId: String = savedStateHandle["scanId"] ?: ""

    init {
        onEvent(ScanResultEvent.Load)
    }

    fun onEvent(event: ScanResultEvent) {
        viewModelScope.launch { handleEvent(event) }
    }

    override suspend fun handleEvent(event: ScanResultEvent) {
        when (event) {
            ScanResultEvent.Load -> {
                setState { copy(isLoading = true, error = null) }
                ioLaunch {
                    when (val result = getScanById(scanId)) {
                        is Result.Success ->
                            setState { copy(scan = result.data, isLoading = false) }
                        is Result.Error ->
                            setState { copy(isLoading = false, error = result.message) }
                    }
                }
            }
        }
    }
}
