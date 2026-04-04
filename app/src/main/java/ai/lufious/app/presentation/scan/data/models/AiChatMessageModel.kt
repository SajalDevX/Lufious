package ai.lufious.app.presentation.scan.data.models

data class AiChatMessageModel(
    val role: String = "user",   // "user" or "assistant"
    val content: String = "",
    val timestamp: Long = 0L
)
