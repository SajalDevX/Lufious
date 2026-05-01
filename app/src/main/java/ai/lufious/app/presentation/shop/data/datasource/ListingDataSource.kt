package ai.lufious.app.presentation.shop.data.datasource

import ai.lufious.app.core.network.LufiousApi
import ai.lufious.app.core.network.dto.ListingCreateRequest
import ai.lufious.app.core.network.dto.toModel
import ai.lufious.app.presentation.shop.data.models.ListingCategory
import ai.lufious.app.presentation.shop.data.models.ListingModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ListingDataSource @Inject constructor(
    private val api: LufiousApi
) {

    suspend fun getListings(category: String? = null, query: String? = null): List<ListingModel> {
        val effectiveCategory = category?.takeIf { it != ListingCategory.ALL && it.isNotBlank() }
        val effectiveQuery = query?.takeIf { it.isNotBlank() }
        return api.listListings(category = effectiveCategory, q = effectiveQuery)
            .items
            .map { it.toModel() }
    }

    suspend fun getListingById(listingId: String): ListingModel =
        api.getListing(listingId).toModel()

    suspend fun createListing(listing: ListingModel): ListingModel =
        api.createListing(
            ListingCreateRequest(
                title = listing.title,
                description = listing.description,
                price = listing.price,
                category = listing.category,
                photoUrl = listing.photoUrls.firstOrNull()
            )
        ).toModel()

    suspend fun getWishlistIds(): List<String> =
        api.getWishlist().listingIds

    suspend fun getWishlistListings(): List<ListingModel> =
        api.getWishlist().items.map { it.toModel() }

    suspend fun toggleWishlist(listingId: String): Boolean {
        val existing = api.getWishlist().listingIds
        return if (listingId in existing) {
            api.removeFromWishlist(listingId)
            false
        } else {
            api.addToWishlist(listingId)
            true
        }
    }
}
