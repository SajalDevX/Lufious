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
