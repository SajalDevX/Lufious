package ai.lufious.app.presentation.scan.data.repository

import ai.lufious.app.core.utils.Result
import ai.lufious.app.presentation.scan.data.models.AiChatMessageModel

interface AiChatRepository {
    suspend fun sendMessage(
        speciesName: String,
        healthStatus: String,
        diagnosis: String,
        userMessage: String
    ): Result<AiChatMessageModel>
}
