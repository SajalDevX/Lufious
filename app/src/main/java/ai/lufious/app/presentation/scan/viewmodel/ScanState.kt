package ai.lufious.app.presentation.scan.viewmodel

import ai.lufious.app.presentation.scan.data.models.AgentKey
import ai.lufious.app.presentation.scan.data.models.ScanResultModel

data class ScanState(
    val isScanning: Boolean = false,
    val scanHistory: List<ScanResultModel> = emptyList(),
    val isHistoryLoading: Boolean = false,
    val error: String? = null,
    val selectedAgent: AgentKey? = null
)
