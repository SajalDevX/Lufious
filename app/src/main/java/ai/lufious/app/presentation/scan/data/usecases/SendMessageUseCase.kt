package ai.lufious.app.presentation.scan.data.usecases

import ai.lufious.app.core.utils.Result
import ai.lufious.app.presentation.scan.data.models.AiChatMessageModel
import ai.lufious.app.presentation.scan.data.repository.AiChatRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(private val repository: AiChatRepository) {
    suspend operator fun invoke(
        speciesName: String,
        healthStatus: String,
        diagnosis: String,
        userMessage: String
    ): Result<AiChatMessageModel> =
        repository.sendMessage(speciesName, healthStatus, diagnosis, userMessage)
}
