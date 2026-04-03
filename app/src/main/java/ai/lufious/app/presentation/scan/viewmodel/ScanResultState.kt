package ai.lufious.app.presentation.scan.viewmodel

import ai.lufious.app.presentation.scan.data.models.ScanResultModel

data class ScanResultState(
    val scan: ScanResultModel? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)
