package ai.lufious.app.presentation.scan.data.repository

import ai.lufious.app.core.utils.Result
import ai.lufious.app.presentation.scan.data.datasource.ScanDataSource
import ai.lufious.app.presentation.scan.data.models.ScanResultModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException

class ScanRepositoryImpl @Inject constructor(
    private val ds: ScanDataSource
) : ScanRepository {

    override suspend fun scanPlant(imageBytes: ByteArray): Result<ScanResultModel> =
        wrap { ds.scanPlant(imageBytes) }

    override suspend fun saveScan(
        scan: ScanResultModel,
        imageBytes: ByteArray?,
        agents: List<String>?
    ): Result<ScanResultModel> =
        wrap { ds.saveScan(scan, imageBytes, agents) }

    override suspend fun getScanHistory(): Result<List<ScanResultModel>> =
        wrap { ds.getScanHistory() }

    override suspend fun getScanById(scanId: String): Result<ScanResultModel> =
        wrap { ds.getScanById(scanId) }

    private suspend fun <T> wrap(block: suspend () -> T): Result<T> =
        try {
            Result.Success(block())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Error(e.message ?: "An unknown error occurred")
        }
}
