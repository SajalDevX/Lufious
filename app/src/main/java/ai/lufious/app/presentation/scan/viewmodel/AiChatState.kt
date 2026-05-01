package ai.lufious.app.presentation.scan.viewmodel

import ai.lufious.app.presentation.scan.data.models.AiChatMessageModel

data class AiChatState(
    val messages: List<AiChatMessageModel> = emptyList(),
    val inputText: String = "",
    val isReplying: Boolean = false,
    val speciesName: String = "",
    val healthStatus: String = "healthy",
    val diagnosis: String = ""
)
