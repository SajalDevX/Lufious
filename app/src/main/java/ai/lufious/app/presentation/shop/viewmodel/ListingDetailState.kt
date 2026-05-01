package ai.lufious.app.presentation.shop.viewmodel

import ai.lufious.app.presentation.shop.data.models.ListingModel

data class ListingDetailState(
    val listing: ListingModel? = null,
    val isWishlisted: Boolean = false,
    val isLoading: Boolean = false,
    val isWishlistLoading: Boolean = false,
    val error: String? = null
)
