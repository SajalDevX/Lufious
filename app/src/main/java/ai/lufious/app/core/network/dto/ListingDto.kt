package ai.lufious.app.core.network.dto

import ai.lufious.app.presentation.shop.data.models.ListingModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ListingDto(
    @SerialName("_id") val id: String,
    val sellerId: String,
    val title: String,
    val description: String = "",
    val price: Double,
    val category: String,
    val photoUrl: String? = null,
    val currency: String = "INR",
    val createdAt: Long = 0L,
    val updatedAt: Long? = null,
    val status: String = "active"
)

@Serializable
data class ListingPageDto(
    val items: List<ListingDto>,
    val page: Int,
    val size: Int,
    val total: Int
)

@Serializable
data class ListingCreateRequest(
    val title: String,
    val description: String = "",
    val price: Double,
    val category: String,
    val photoUrl: String? = null
)

@Serializable
data class ListingPatchRequest(
    val title: String? = null,
    val description: String? = null,
    val price: Double? = null,
    val category: String? = null,
    val photoUrl: String? = null,
    val status: String? = null
)

@Serializable
data class WishlistResponse(
    val items: List<ListingDto>,
    val listingIds: List<String>
)

fun ListingDto.toModel(): ListingModel = ListingModel(
    id = id,
    sellerId = sellerId,
    title = title,
    description = description,
    price = price,
    category = category,
    photoUrls = listOfNotNull(photoUrl?.takeIf { it.isNotEmpty() }),
    currency = currency,
    createdAt = createdAt,
    status = status
)
