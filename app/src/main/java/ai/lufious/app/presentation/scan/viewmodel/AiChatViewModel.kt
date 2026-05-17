package ai.lufious.app.presentation.scan.viewmodel

import ai.lufious.app.core.utils.BaseViewModel
import ai.lufious.app.core.utils.DispatcherProvider
import ai.lufious.app.core.utils.Result
import ai.lufious.app.presentation.scan.data.models.AiChatMessageModel
import ai.lufious.app.presentation.scan.data.usecases.GetScanByIdUseCase
import ai.lufious.app.presentation.scan.data.usecases.LoadChatHistoryUseCase
import ai.lufious.app.presentation.scan.data.usecases.SendMessageUseCase
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AiChatViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getScanById: GetScanByIdUseCase,
    private val loadHistory: LoadChatHistoryUseCase,
    private val sendMessage: SendMessageUseCase,
    dispatchers: DispatcherProvider
) : BaseViewModel<AiChatEvent, AiChatState>(AiChatState(), dispatchers) {

    private val scanId: String = savedStateHandle["scanId"] ?: ""

    init {
        loadScanContext()
    }

    private fun loadScanContext() {
        ioLaunch {
            // Pull scan meta (species/health) AND the seed/assistant history
            // straight from backend. Backend embeds messages[] on ScanDoc and the
            // very first assistant message is generated server-side at scan time.
            when (val scanRes = getScanById(scanId)) {
                is Result.Success -> {
                    val scan = scanRes.data ?: return@ioLaunch
                    setState {
                        copy(
                            speciesName = scan.speciesName,
                            healthStatus = scan.healthStatus,
                            diagnosis = scan.diagnosis
                        )
                    }
                }
                is Result.Error -> Unit
            }
            when (val historyRes = loadHistory(scanId)) {
                is Result.Success -> {
                    val msgs = historyRes.data.orEmpty()
                    if (msgs.isNotEmpty()) setState { copy(messages = msgs) }
                }
                is Result.Error -> Unit
            }
        }
    }

    fun onEvent(event: AiChatEvent) {
        viewModelScope.launch { handleEvent(event) }
    }

    override suspend fun handleEvent(event: AiChatEvent) {
        when (event) {
            is AiChatEvent.InputChanged ->
                setState { copy(inputText = event.text) }

            AiChatEvent.Send -> {
                val text = state.value.inputText.trim()
                if (text.isBlank() || state.value.isReplying) return
                val optimistic = AiChatMessageModel(
                    role = "user",
                    content = text,
                    timestamp = System.currentTimeMillis()
                )
                setState { copy(messages = messages + optimistic, inputText = "", isReplying = true) }
                ioLaunch {
                    when (val result = sendMessage(scanId = scanId, userMessage = text)) {
                        is Result.Success -> {
                            val (_, assistant) = result.data ?: return@ioLaunch
                            setState { copy(messages = messages + assistant, isReplying = false) }
                        }
                        is Result.Error ->
                            setState { copy(isReplying = false) }
                    }
                }
            }
        }
    }
}
