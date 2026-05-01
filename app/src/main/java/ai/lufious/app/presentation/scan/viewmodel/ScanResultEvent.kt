package ai.lufious.app.presentation.scan.viewmodel

sealed class ScanResultEvent {
    object Load : ScanResultEvent()
}
