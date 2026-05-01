package ai.lufious.app.presentation.shop.data.datasource

import ai.lufious.app.core.firebase.utils.ListingFields
import ai.lufious.app.core.firebase.utils.WishlistFields
import ai.lufious.app.core.local_cache.LocalCacheManager
import ai.lufious.app.presentation.shop.data.models.ListingModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class ListingDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val localCache: LocalCacheManager
) {
    private val uid get() = localCache.getUser()?.uid ?: error("User not logged in")

    private fun listingsRef() =
        firestore.collection(ListingFields.MARKETPLACE)
            .document("global")
            .collection(ListingFields.COLLECTION)

    private fun wishlistRef() =
        firestore.collection("users").document(uid).collection(WishlistFields.COLLECTION)

    suspend fun getListings(category: String? = null): List<ListingModel> {
        val base: Query = listingsRef()
            .whereEqualTo(ListingFields.STATUS, "active")
            .orderBy(ListingFields.CREATED_AT, Query.Direction.DESCENDING)
        val query = if (category != null) base.whereEqualTo(ListingFields.CATEGORY, category) else base
        return query.get().await().documents.mapNotNull { it.toListingModel() }
    }

    suspend fun getListingById(listingId: String): ListingModel =
        listingsRef().document(listingId).get().await().toListingModel()
            ?: error("Listing $listingId not found")

    suspend fun createListing(listing: ListingModel): ListingModel {
        val doc = listingsRef().document()
        val withId = listing.copy(
            id = doc.id,
            sellerId = uid,
            createdAt = System.currentTimeMillis(),
            status = "active"
        )
        doc.set(withId.toMap()).await()
        return withId
    }

    suspend fun getWishlistIds(): List<String> =
        wishlistRef().get().await().documents.map { it.id }

    suspend fun toggleWishlist(listingId: String): Boolean {
        val doc = wishlistRef().document(listingId)
        val exists = doc.get().await().exists()
        if (exists) {
            doc.delete().await()
        } else {
            doc.set(mapOf(WishlistFields.ADDED_AT to System.currentTimeMillis())).await()
        }
        return !exists
    }

    private fun ListingModel.toMap(): Map<String, Any?> = mapOf(
        ListingFields.SELLER_ID to sellerId,
        ListingFields.TITLE to title,
        ListingFields.DESCRIPTION to description,
        ListingFields.PRICE to price,
        ListingFields.CATEGORY to category,
        ListingFields.PHOTO_URLS to photoUrls,
        ListingFields.CREATED_AT to createdAt,
        ListingFields.STATUS to status
    )

    @Suppress("UNCHECKED_CAST")
    private fun DocumentSnapshot.toListingModel(): ListingModel? = try {
        ListingModel(
            id = id,
            sellerId = getString(ListingFields.SELLER_ID) ?: "",
            title = getString(ListingFields.TITLE) ?: "",
            description = getString(ListingFields.DESCRIPTION) ?: "",
            price = getDouble(ListingFields.PRICE) ?: 0.0,
            category = getString(ListingFields.CATEGORY) ?: "",
            photoUrls = get(ListingFields.PHOTO_URLS) as? List<String> ?: emptyList(),
            createdAt = getLong(ListingFields.CREATED_AT) ?: 0L,
            status = getString(ListingFields.STATUS) ?: "active"
        )
    } catch (e: Exception) { null }
}
