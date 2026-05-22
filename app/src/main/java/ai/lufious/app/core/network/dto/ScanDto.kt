package ai.lufious.app.core.network.dto

import ai.lufious.app.presentation.scan.data.models.ScanResultModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ScanMessageDto(
    val role: String,
    val content: String,
    val agentKey: String? = null,
    val mentions: List<String> = emptyList(),
    val createdAt: Long
)

@Serializable
data class ScanMessageRequest(val content: String)

@Serializable
data class ScanMessagePairDto(
    val user: ScanMessageDto,
    val replies: List<ScanMessageDto> = emptyList(),
    // Legacy field kept for older clients; new flow uses `replies`.
    val assistant: ScanMessageDto? = null
)

@Serializable
data class ScanAgentDto(
    val key: String,
    val name: String,
    val emoji: String,
    val aliases: List<String> = emptyList()
)

@Serializable
data class ScanAgentListResponse(val items: List<ScanAgentDto>)

@Serializable
data class ScanDto(
    @SerialName("_id") val id: String,
    val userId: String,
    val speciesName: String = "",
    val commonName: String = "",
    val confidence: Float = 0f,
    val healthStatus: String = "healthy",
    val diagnosis: String = "",
    val carePlan: String = "",
    val photoUrl: String? = null,
    val messages: List<ScanMessageDto> = emptyList(),
    val aiSummary: String? = null,
    val agentsReady: List<String> = emptyList(),
    val timestamp: Long
)

@Serializable
data class ScanListResponse(val items: List<ScanDto>)

@Serializable
data class ScanCreateRequest(
    val photoUrl: String,
    val agents: List<String>? = null
)

@Serializable
data class SignedUploadRequest(
    val kind: String,
    val refId: String? = null,
    val contentType: String? = null
)

@Serializable
data class SignedUploadResponse(
    val uploadUrl: String,
    val downloadUrl: String,
    val expiresAt: Long
)

fun ScanDto.toModel(): ScanResultModel = ScanResultModel(
    id = id,
    speciesName = speciesName,
    commonName = commonName,
    confidence = confidence,
    healthStatus = healthStatus,
    diagnosis = diagnosis,
    carePlan = carePlan,
    photoUrl = photoUrl ?: "",
    timestamp = timestamp
)
