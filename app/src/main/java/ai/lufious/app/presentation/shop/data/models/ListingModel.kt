package ai.lufious.app.presentation.shop.data.models

data class ListingModel(
    val id: String = "",
    val sellerId: String = "",
    val title: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val category: String = "",
    val photoUrls: List<String> = emptyList(),
    val createdAt: Long = 0L,
    val status: String = "active"
)
