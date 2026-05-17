package ai.lufious.app.presentation.scan.data.repository

import ai.lufious.app.core.utils.Result
import ai.lufious.app.presentation.scan.data.models.AiChatMessageModel

interface AiChatRepository {
    suspend fun loadHistory(scanId: String): Result<List<AiChatMessageModel>>
    suspend fun sendMessage(
        scanId: String,
        userMessage: String
    ): Result<Pair<AiChatMessageModel, AiChatMessageModel>>
}
