package ai.lufious.app.presentation.shop.viewmodel

import ai.lufious.app.presentation.shop.data.models.ListingCategory

data class CreateListingState(
    val title: String = "",
    val description: String = "",
    val price: String = "",
    val category: String = ListingCategory.PRODUCE,
    val isLoading: Boolean = false,
    val isSubmitEnabled: Boolean = false
)
