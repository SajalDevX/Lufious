package ai.lufious.app.presentation.scan.data.models

data class ScanResultModel(
    val id: String = "",
    val speciesName: String = "",
    val commonName: String = "",
    val confidence: Float = 0f,
    val healthStatus: String = "healthy",
    val diagnosis: String = "",
    val carePlan: String = "",
    val photoUrl: String = "",
    val timestamp: Long = 0L
)
