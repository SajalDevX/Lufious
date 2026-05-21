package ai.lufious.app.presentation.scan.viewmodel

import ai.lufious.app.presentation.scan.data.models.AiChatMessageModel

data class AiChatState(
    val messages: List<AiChatMessageModel> = emptyList(),
    val inputText: String = "",
    val isReplying: Boolean = false,
    val speciesName: String = "",
    val commonName: String = "",
    val healthStatus: String = "healthy",
    val diagnosis: String = "",
    val plantPhotoUrl: String = "",
    val quickReplies: List<QuickReply> = defaultQuickReplies
)

data class QuickReply(
    val id: String,
    val label: String,
    val prompt: String,
    val emoji: String
)

private val defaultQuickReplies = listOf(
    QuickReply("light", "By a window\n(east / west)", "It sits by an east/west-facing window. Is the light okay?", "☀️"),
    QuickReply("room", "Living room\nlow light", "It's in a low-light living room. Will it still do well?", "🛋️"),
    QuickReply("age", "Just got it\nrecently", "I just got this plant recently — anything I should do in the first week?", "🌱")
)
