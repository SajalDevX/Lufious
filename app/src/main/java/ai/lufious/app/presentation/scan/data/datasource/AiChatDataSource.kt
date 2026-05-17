package ai.lufious.app.presentation.scan.data.datasource

import ai.lufious.app.core.network.LufiousApi
import ai.lufious.app.core.network.dto.ScanMessageDto
import ai.lufious.app.core.network.dto.ScanMessageRequest
import ai.lufious.app.presentation.scan.data.models.AiChatMessageModel
import javax.inject.Inject

class AiChatDataSource @Inject constructor(
    private val api: LufiousApi
) {

    suspend fun loadHistory(scanId: String): List<AiChatMessageModel> {
        val scan = api.getScan(scanId)
        return scan.messages.map { it.toModel() }
    }

    suspend fun sendMessage(scanId: String, content: String): Pair<AiChatMessageModel, AiChatMessageModel> {
        val pair = api.postScanMessage(scanId, ScanMessageRequest(content = content))
        return pair.user.toModel() to pair.assistant.toModel()
    }

    private fun ScanMessageDto.toModel(): AiChatMessageModel =
        AiChatMessageModel(role = role, content = content, timestamp = createdAt)
}
