package ai.lufious.app.presentation.scan.data.usecases

import ai.lufious.app.core.utils.Result
import ai.lufious.app.presentation.scan.data.models.AiChatMessageModel
import ai.lufious.app.presentation.scan.data.repository.AiChatRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(private val repository: AiChatRepository) {
    suspend operator fun invoke(
        scanId: String,
        userMessage: String
    ): Result<Pair<AiChatMessageModel, List<AiChatMessageModel>>> =
        repository.sendMessage(scanId, userMessage)
}

class LoadChatHistoryUseCase @Inject constructor(private val repository: AiChatRepository) {
    suspend operator fun invoke(scanId: String): Result<List<AiChatMessageModel>> =
        repository.loadHistory(scanId)
}
