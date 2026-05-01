package ai.lufious.app.presentation.auth.data.datasource

import ai.lufious.app.core.firebase.FirestoreManager
import ai.lufious.app.core.firebase.utils.FirestoreModels
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.tasks.await

class FirebaseAuthDataSource @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestoreManager: FirestoreManager,
    private val firestore: FirebaseFirestore
) {
    private suspend fun signInWithCredential(cred: AuthCredential): FirebaseUser =
        suspendCoroutine { cont ->
            auth.signInWithCredential(cred)
                .addOnSuccessListener { cont.resume(it.user!!) }
                .addOnFailureListener { cont.resumeWithException(it) }
        }

    suspend fun loginWithEmail(email: String, pw: String): FirebaseUser {
        val user = suspendCoroutine<FirebaseUser> { cont ->
            auth.signInWithEmailAndPassword(email, pw)
                .addOnSuccessListener { cont.resume(it.user!!) }
                .addOnFailureListener { cont.resumeWithException(it) }
        }
        upsertUserProfile(user, provider = "email")
        return user
    }

    suspend fun signupWithEmail(email: String, pw: String): FirebaseUser {
        val user = try {
            suspendCoroutine<FirebaseUser> { cont ->
                auth.createUserWithEmailAndPassword(email, pw)
                    .addOnSuccessListener { cont.resume(it.user!!) }
                    .addOnFailureListener { cont.resumeWithException(it) }
            }
        } catch (e: FirebaseAuthUserCollisionException) {
            throw Exception("Email already in use. Please log in.")
        }
        upsertUserProfile(user, provider = "email", isNew = true)
        return user
    }

    suspend fun loginWithGoogle(idToken: String): FirebaseUser {
        val cred = GoogleAuthProvider.getCredential(idToken, null)
        val user = signInWithCredential(cred)
        upsertUserProfile(user, provider = "google")
        return user
    }

    suspend fun signupWithGoogle(idToken: String): FirebaseUser {
        val cred = GoogleAuthProvider.getCredential(idToken, null)
        val user = signInWithCredential(cred)
        upsertUserProfile(user, provider = "google", isNew = true)
        return user
    }

    suspend fun loginWithFacebook(accessToken: String): FirebaseUser {
        val cred = FacebookAuthProvider.getCredential(accessToken)
        val user = signInWithCredential(cred)
        upsertUserProfile(user, provider = "facebook")
        return user
    }

    fun signOut() = auth.signOut()

    val currentUser: FirebaseUser? get() = auth.currentUser

    private suspend fun upsertUserProfile(
        user: FirebaseUser,
        provider: String,
        isNew: Boolean = false
    ) {
        val now = System.currentTimeMillis()
        val data = mutableMapOf<String, Any?>(
            FirestoreModels.User.UID to user.uid,
            FirestoreModels.User.EMAIL to (user.email ?: ""),
            FirestoreModels.User.NAME to (user.displayName ?: ""),
            FirestoreModels.User.PHOTO_URL to user.photoUrl?.toString(),
            FirestoreModels.User.PHONE to user.phoneNumber,
            FirestoreModels.User.PROVIDER to provider,
            FirestoreModels.User.UPDATED_AT to now
        )
        if (isNew) data[FirestoreModels.User.CREATED_AT] = now

        runCatching {
            val token = FirebaseMessaging.getInstance().token.await()
            data[FirestoreModels.User.FCM_TOKEN] = token
        }

        firestore.collection(FirestoreModels.User.collection)
            .document(user.uid)
            .set(data, SetOptions.merge())
            .await()
    }
}
