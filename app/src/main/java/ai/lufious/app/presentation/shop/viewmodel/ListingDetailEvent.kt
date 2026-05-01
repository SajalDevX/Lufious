package ai.lufious.app.presentation.shop.viewmodel

sealed class ListingDetailEvent {
    object ToggleWishlist : ListingDetailEvent()
}
