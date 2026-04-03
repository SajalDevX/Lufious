package ai.lufious.app.presentation.scan.data.repository

import ai.lufious.app.core.utils.Result
import ai.lufious.app.presentation.scan.data.models.ScanResultModel

interface ScanRepository {
    suspend fun scanPlant(imageBytes: ByteArray): Result<ScanResultModel>
    suspend fun saveScan(scan: ScanResultModel): Result<ScanResultModel>
    suspend fun getScanHistory(): Result<List<ScanResultModel>>
    suspend fun getScanById(scanId: String): Result<ScanResultModel>
}
