package ai.lufious.app.presentation.scan.data.datasource

import ai.lufious.app.core.network.LufiousApi
import ai.lufious.app.core.network.SignedUploader
import ai.lufious.app.core.network.dto.ScanCreateRequest
import ai.lufious.app.core.network.dto.SignedUploadRequest
import ai.lufious.app.core.network.dto.toModel
import ai.lufious.app.presentation.scan.data.models.ScanResultModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScanDataSource @Inject constructor(
    private val api: LufiousApi,
    private val uploader: SignedUploader
) {
    /**
     * Backwards-compatible signature used by the existing repository:
     * uploads bytes via the backend's signed-URL flow and returns a stub
     * ScanResultModel carrying the photoUrl. The full identification result
     * is produced server-side by [saveScan] which calls Pl@ntNet.
     */
    suspend fun scanPlant(imageBytes: ByteArray): ScanResultModel {
        val signed = api.signUpload(SignedUploadRequest(kind = "scan"))
        uploader.upload(signed.uploadUrl, imageBytes)
        return ScanResultModel(photoUrl = signed.downloadUrl)
    }

    suspend fun saveScan(
        scan: ScanResultModel,
        @Suppress("UNUSED_PARAMETER") imageBytes: ByteArray? = null
    ): ScanResultModel {
        require(scan.photoUrl.isNotEmpty()) { "photoUrl required" }
        return api.createScan(ScanCreateRequest(photoUrl = scan.photoUrl)).toModel()
    }

    suspend fun getScanHistory(): List<ScanResultModel> =
        api.listScans().items.map { it.toModel() }

    suspend fun getScanById(scanId: String): ScanResultModel =
        api.getScan(scanId).toModel()
}
