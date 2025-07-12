package ai.lufious.app.presentation.auth.data.models


data class UserModel(
    val uid: String,
    val email: String,
    val displayName: String? = null,
    val phoneNumber: String? = null,
    val photoUrl: String? = null,
    val creationTimestamp: Long? = null,
    val lastSignInTimestamp: Long? = null
)
