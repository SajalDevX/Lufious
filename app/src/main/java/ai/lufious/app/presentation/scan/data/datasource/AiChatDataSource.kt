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

    suspend fun sendMessage(scanId: String, content: String): Pair<AiChatMessageModel, List<AiChatMessageModel>> {
        val pair = api.postScanMessage(scanId, ScanMessageRequest(content = content))
        val replies = when {
            pair.replies.isNotEmpty() -> pair.replies.map { it.toModel() }
            pair.assistant != null -> listOf(pair.assistant.toModel())
            else -> emptyList()
        }
        return pair.user.toModel() to replies
    }

    private fun ScanMessageDto.toModel(): AiChatMessageModel =
        AiChatMessageModel(role = role, content = content, timestamp = createdAt)
}
