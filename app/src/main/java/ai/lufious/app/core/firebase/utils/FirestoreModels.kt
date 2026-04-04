package ai.lufious.app.core.firebase.utils

sealed class FirestoreModels(val collection: String) {
    object User : FirestoreModels("users") {
        const val NAME = "name"
        const val EMAIL = "email"
        const val AGE = "age"
    }
}

object PlantFields {
    const val COLLECTION = "plants"
    const val NICKNAME = "nickname"
    const val SPECIES = "species"
    const val PHOTO_URL = "photoUrl"
    const val LOCATION_TAG = "locationTag"
    const val WATERING_INTERVAL_DAYS = "wateringIntervalDays"
    const val FERTILIZING_INTERVAL_DAYS = "fertilizingIntervalDays"
    const val LAST_WATERED = "lastWatered"
    const val LAST_FERTILIZED = "lastFertilized"
    const val ADDED_AT = "addedAt"
    const val HEALTH_STATUS = "healthStatus"
}

object LogFields {
    const val COLLECTION = "logs"
    const val TYPE = "type"
    const val NOTE = "note"
    const val TIMESTAMP = "timestamp"
}

object ListingFields {
    const val MARKETPLACE = "marketplace"
    const val COLLECTION = "listings"
    const val SELLER_ID = "sellerId"
    const val TITLE = "title"
    const val DESCRIPTION = "description"
    const val PRICE = "price"
    const val CATEGORY = "category"
    const val PHOTO_URLS = "photoUrls"
    const val CREATED_AT = "createdAt"
    const val STATUS = "status"
}

object WishlistFields {
    const val COLLECTION = "wishlist"
    const val ADDED_AT = "addedAt"
}

object PhotoFields {
    const val COLLECTION = "photos"
    const val NOTE = "note"
    const val PHOTO_URL = "photoUrl"
    const val TIMESTAMP = "timestamp"
}

object ScanFields {
    const val COLLECTION = "scans"
    const val SPECIES_NAME = "speciesName"
    const val COMMON_NAME = "commonName"
    const val CONFIDENCE = "confidence"
    const val HEALTH_STATUS = "healthStatus"
    const val DIAGNOSIS = "diagnosis"
    const val CARE_PLAN = "carePlan"
    const val TIMESTAMP = "timestamp"
}
