package ai.lufious.app.presentation.scan.data.usecases

import ai.lufious.app.core.utils.Result
import ai.lufious.app.presentation.scan.data.models.ScanResultModel
import ai.lufious.app.presentation.scan.data.repository.ScanRepository
import jakarta.inject.Inject

class ScanPlantUseCase @Inject constructor(private val repository: ScanRepository) {
    suspend operator fun invoke(imageBytes: ByteArray): Result<ScanResultModel> {
        val scanResult = when (val result = repository.scanPlant(imageBytes)) {
            is Result.Success -> result.data ?: return Result.Error("No scan result returned")
            is Result.Error -> return result
        }
        return repository.saveScan(scanResult)
    }
}

class GetScanHistoryUseCase @Inject constructor(private val repository: ScanRepository) {
    suspend operator fun invoke(): Result<List<ScanResultModel>> = repository.getScanHistory()
}

class GetScanByIdUseCase @Inject constructor(private val repository: ScanRepository) {
    suspend operator fun invoke(scanId: String): Result<ScanResultModel> =
        repository.getScanById(scanId)
}
