package ai.lufious.app.presentation.shop.data.models

data class ListingModel(
    val id: String = "",
    val sellerId: String = "",
    val title: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val category: String = "",
    val photoUrls: List<String> = emptyList(),
    val currency: String = "INR",
    val createdAt: Long = 0L,
    val status: String = "active"
)

object ListingCategory {
    const val ALL = "all"
    const val CONIFERS = "conifers"
    const val HOUSEPLANT = "houseplant"
    const val PERENNIAL = "perennial"
    const val SHRUBS = "shrubs"
    const val FLOWERING = "flowering"

    val browseOptions = listOf(ALL, CONIFERS, HOUSEPLANT, PERENNIAL, SHRUBS, FLOWERING)
    val listingOptions = browseOptions.filterNot { it == ALL }

    fun displayName(slug: String): String = when (slug) {
        ALL -> "All"
        CONIFERS -> "Conifers"
        HOUSEPLANT -> "Houseplant"
        PERENNIAL -> "Perennial"
        SHRUBS -> "Shrubs"
        FLOWERING -> "Flowering"
        else -> slug.replaceFirstChar { it.uppercase() }
    }
}
