package ai.lufious.app.presentation.scan.viewmodel

sealed class AiChatEvent {
    data class InputChanged(val text: String) : AiChatEvent()
    object Send : AiChatEvent()
}
