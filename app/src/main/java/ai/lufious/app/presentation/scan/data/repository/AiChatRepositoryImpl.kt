package ai.lufious.app.presentation.scan.data.repository

import ai.lufious.app.core.utils.Result
import ai.lufious.app.presentation.scan.data.datasource.AiChatDataSource
import ai.lufious.app.presentation.scan.data.models.AiChatMessageModel
import jakarta.inject.Inject
import kotlinx.coroutines.CancellationException

class AiChatRepositoryImpl @Inject constructor(
    private val ds: AiChatDataSource
) : AiChatRepository {

    override suspend fun sendMessage(
        speciesName: String,
        healthStatus: String,
        diagnosis: String,
        userMessage: String
    ): Result<AiChatMessageModel> = wrap {
        ds.sendMessage(speciesName, healthStatus, diagnosis, userMessage)
    }

    private suspend fun <T> wrap(block: suspend () -> T): Result<T> =
        try {
            Result.Success(block())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
}
