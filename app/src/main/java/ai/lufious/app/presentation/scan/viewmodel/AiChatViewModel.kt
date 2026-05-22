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
import kotlinx.coroutines.delay
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
            // Backend POST /api/scans returns with empty species + empty messages
            // while PlantNet + AI seed run asynchronously. Poll the scan doc
            // until both meta (species) and at least one message are populated.
            var attempts = 0
            val maxAttempts = 30 // ~45s upper bound
            var metaApplied = false
            var messagesApplied = false
            while (attempts < maxAttempts && (!metaApplied || !messagesApplied)) {
                val res = getScanById(scanId)
                if (res is Result.Success) {
                    val scan = res.data
                    if (scan != null) {
                        if (!metaApplied && scan.speciesName.isNotBlank()) {
                            setState {
                                copy(
                                    speciesName = scan.speciesName,
                                    commonName = scan.commonName,
                                    healthStatus = scan.healthStatus,
                                    diagnosis = scan.diagnosis,
                                    plantPhotoUrl = scan.photoUrl
                                )
                            }
                            metaApplied = true
                        }
                    }
                }
                if (!messagesApplied) {
                    val hRes = loadHistory(scanId)
                    if (hRes is Result.Success) {
                        val msgs = hRes.data.orEmpty()
                        if (msgs.isNotEmpty()) {
                            setState { copy(messages = msgs) }
                            messagesApplied = true
                        }
                    }
                }
                if (metaApplied && messagesApplied) return@ioLaunch
                attempts++
                delay(1200L)
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

            is AiChatEvent.QuickReplyTapped -> {
                if (state.value.isReplying) return
                setState { copy(inputText = event.reply.prompt) }
                handleEvent(AiChatEvent.Send)
            }

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
                            val (_, replies) = result.data ?: return@ioLaunch
                            setState { copy(messages = messages + replies, isReplying = false) }
                        }
                        is Result.Error ->
                            setState { copy(isReplying = false) }
                    }
                }
            }
        }
    }
}
