package ai.lufious.app.presentation.shop.viewmodel

sealed class CreateListingEvent {
    data class TitleChanged(val value: String) : CreateListingEvent()
    data class DescriptionChanged(val value: String) : CreateListingEvent()
    data class PriceChanged(val value: String) : CreateListingEvent()
    data class CategoryChanged(val value: String) : CreateListingEvent()
    object Submit : CreateListingEvent()
}
