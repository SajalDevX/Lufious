package ai.lufious.app.presentation.shop.viewmodel

sealed class ShopEvent {
    data class CategoryChanged(val category: String) : ShopEvent()
    data class ToggleWishlist(val listingId: String) : ShopEvent()
    object Refresh : ShopEvent()
}
