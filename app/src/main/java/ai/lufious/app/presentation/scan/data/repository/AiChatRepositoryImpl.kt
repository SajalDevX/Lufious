package ai.lufious.app.presentation.scan.data.repository

import ai.lufious.app.core.utils.Result
import ai.lufious.app.presentation.scan.data.datasource.AiChatDataSource
import ai.lufious.app.presentation.scan.data.models.AiChatMessageModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException

class AiChatRepositoryImpl @Inject constructor(
    private val ds: AiChatDataSource
) : AiChatRepository {

    override suspend fun loadHistory(scanId: String): Result<List<AiChatMessageModel>> = wrap {
        ds.loadHistory(scanId)
    }

    override suspend fun sendMessage(
        scanId: String,
        userMessage: String
    ): Result<Pair<AiChatMessageModel, List<AiChatMessageModel>>> = wrap {
        ds.sendMessage(scanId, userMessage)
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
