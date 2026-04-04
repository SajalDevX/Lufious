package ai.lufious.app.presentation.scan.viewmodel

import ai.lufious.app.core.utils.BaseViewModel
import ai.lufious.app.core.utils.DispatcherProvider
import ai.lufious.app.core.utils.Result
import ai.lufious.app.presentation.scan.data.models.AiChatMessageModel
import ai.lufious.app.presentation.scan.data.usecases.GetScanByIdUseCase
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
    private val sendMessage: SendMessageUseCase,
    dispatchers: DispatcherProvider
) : BaseViewModel<AiChatEvent, AiChatState>(AiChatState(), dispatchers) {

    private val scanId: String = savedStateHandle["scanId"] ?: ""

    init {
        loadScanContext()
    }

    private fun loadScanContext() {
        ioLaunch {
            when (val result = getScanById(scanId)) {
                is Result.Success -> {
                    val scan = result.data ?: return@ioLaunch
                    setState {
                        copy(
                            speciesName = scan.speciesName,
                            healthStatus = scan.healthStatus,
                            diagnosis = scan.diagnosis,
                            messages = listOf(
                                AiChatMessageModel(
                                    role = "assistant",
                                    content = "Hi! I'm your AI plant assistant. I've analyzed your ${scan.speciesName} and I'm ready to answer any questions about its care, health, or diagnosis. What would you like to know?",
                                    timestamp = System.currentTimeMillis()
                                )
                            )
                        )
                    }
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
                val userMsg = AiChatMessageModel(
                    role = "user",
                    content = text,
                    timestamp = System.currentTimeMillis()
                )
                setState { copy(messages = messages + userMsg, inputText = "", isReplying = true) }
                ioLaunch {
                    when (val result = sendMessage(
                        speciesName = state.value.speciesName,
                        healthStatus = state.value.healthStatus,
                        diagnosis = state.value.diagnosis,
                        userMessage = text
                    )) {
                        is Result.Success -> {
                            val reply = result.data ?: return@ioLaunch
                            setState { copy(messages = messages + reply, isReplying = false) }
                        }
                        is Result.Error ->
                            setState { copy(isReplying = false) }
                    }
                }
            }
        }
    }
}
