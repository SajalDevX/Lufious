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
    const val PRODUCE = "produce"
    const val SEEDS = "seeds"
    const val SOIL = "soil"
    const val TOOLS = "tools"
    const val PESTICIDES = "pesticides"

    val browseOptions = listOf(ALL, PRODUCE, SEEDS, SOIL, TOOLS, PESTICIDES)
    val listingOptions = browseOptions.filterNot { it == ALL }

    fun displayName(slug: String): String = when (slug) {
        ALL -> "All"
        PRODUCE -> "Produce"
        SEEDS -> "Seeds"
        SOIL -> "Soil & Nutrients"
        TOOLS -> "Tools"
        PESTICIDES -> "Pesticides"
        else -> slug.replaceFirstChar { it.uppercase() }
    }
}
