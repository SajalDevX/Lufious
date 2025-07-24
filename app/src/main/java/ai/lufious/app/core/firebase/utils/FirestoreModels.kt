package ai.lufious.app.core.firebase.utils

sealed class FirestoreModels(val collection: String) {
    object User : FirestoreModels("users") {
        const val NAME = "name"
        const val EMAIL = "email"
        const val AGE = "age"
    }
}

