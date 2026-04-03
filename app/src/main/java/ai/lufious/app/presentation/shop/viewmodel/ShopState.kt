package ai.lufious.app.presentation.shop.viewmodel

import ai.lufious.app.presentation.shop.data.models.ListingCategory
import ai.lufious.app.presentation.shop.data.models.ListingModel

data class ShopState(
    val selectedCategory: String = ListingCategory.ALL,
    val listings: List<ListingModel> = emptyList(),
    val wishlistIds: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val error: String? = null
)
