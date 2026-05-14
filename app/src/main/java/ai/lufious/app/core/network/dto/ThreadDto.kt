package ai.lufious.app.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ThreadDto(
    @SerialName("_id") val id: String,
    val participants: List<String>,
    val listingId: String? = null,
    val lastMessage: String = "",
    val lastMessageAt: Long = 0L,
    val lastSenderId: String? = null,
    val unread: Map<String, Int> = emptyMap(),
    val createdAt: Long = 0L
)

@Serializable
data class ThreadListResponse(val items: List<ThreadDto>)

@Serializable
data class ThreadCreateRequest(
    val recipientId: String,
    val listingId: String? = null
)

@Serializable
data class MessageDto(
    @SerialName("_id") val id: String,
    val threadId: String,
    val senderId: String,
    val body: String,
    val createdAt: Long
)

@Serializable
data class MessageListResponse(val items: List<MessageDto>)

@Serializable
data class MessageCreateRequest(val body: String)
