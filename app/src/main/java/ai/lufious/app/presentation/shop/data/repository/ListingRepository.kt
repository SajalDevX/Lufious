package ai.lufious.app.presentation.shop.data.repository

import ai.lufious.app.core.utils.Result
import ai.lufious.app.presentation.shop.data.models.ListingModel

interface ListingRepository {
    suspend fun getListings(category: String? = null): Result<List<ListingModel>>

    suspend fun createListing(
        title: String,
        description: String,
        price: Double,
        category: String
    ): Result<ListingModel>

    suspend fun getListingById(listingId: String): Result<ListingModel>

    suspend fun toggleWishlist(listingId: String): Result<Boolean>

    suspend fun getWishlistIds(): Result<List<String>>
}
