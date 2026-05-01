package ai.lufious.app.presentation.scan.viewmodel

sealed class ScanEvent {
    data class Scan(val imageBytes: ByteArray) : ScanEvent() {
        override fun equals(other: Any?) = other is Scan && imageBytes.contentEquals(other.imageBytes)
        override fun hashCode() = imageBytes.contentHashCode()
    }
    data class ScanFailed(val message: String) : ScanEvent()
    object LoadHistory : ScanEvent()
}
