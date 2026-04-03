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

object ListingCategory {
    const val ALL = "All"
    const val PLANTS = "Plants"
    const val POTS_AND_PLANTERS = "Pots & Planters"
    const val SOIL_AND_FERTILIZERS = "Soil & Fertilizers"
    const val TOOLS = "Tools"
    const val ACCESSORIES = "Accessories"

    val browseOptions = listOf(
        ALL,
        PLANTS,
        POTS_AND_PLANTERS,
        SOIL_AND_FERTILIZERS,
        TOOLS,
        ACCESSORIES
    )

    val listingOptions = browseOptions.filterNot { it == ALL }
}
