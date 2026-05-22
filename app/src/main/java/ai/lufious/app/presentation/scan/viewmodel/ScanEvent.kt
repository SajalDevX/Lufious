package ai.lufious.app.presentation.scan.viewmodel

import ai.lufious.app.presentation.scan.data.models.AgentKey

sealed class ScanEvent {
    data class SelectAgent(val agent: AgentKey?) : ScanEvent()
    data class Scan(val imageBytes: ByteArray, val agent: AgentKey? = null) : ScanEvent() {
        override fun equals(other: Any?) =
            other is Scan && imageBytes.contentEquals(other.imageBytes) && agent == other.agent
        override fun hashCode(): Int {
            var result = imageBytes.contentHashCode()
            result = 31 * result + (agent?.hashCode() ?: 0)
            return result
        }
    }
    data class ScanFailed(val message: String) : ScanEvent()
    object LoadHistory : ScanEvent()
}
