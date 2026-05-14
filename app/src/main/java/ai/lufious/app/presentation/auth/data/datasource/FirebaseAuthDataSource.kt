package ai.lufious.app.presentation.auth.data.datasource

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseAuthDataSource @Inject constructor(
    private val auth: FirebaseAuth
) {
    private suspend fun signInWithCredential(cred: AuthCredential): FirebaseUser =
        suspendCoroutine { cont ->
            auth.signInWithCredential(cred)
                .addOnSuccessListener { cont.resume(it.user!!) }
                .addOnFailureListener { cont.resumeWithException(it) }
        }

    suspend fun loginWithEmail(email: String, pw: String): FirebaseUser =
        suspendCoroutine { cont ->
            auth.signInWithEmailAndPassword(email, pw)
                .addOnSuccessListener { cont.resume(it.user!!) }
                .addOnFailureListener { cont.resumeWithException(it) }
        }

    suspend fun signupWithEmail(email: String, pw: String): FirebaseUser {
        return try {
            suspendCoroutine { cont ->
                auth.createUserWithEmailAndPassword(email, pw)
                    .addOnSuccessListener { cont.resume(it.user!!) }
                    .addOnFailureListener { cont.resumeWithException(it) }
            }
        } catch (e: FirebaseAuthUserCollisionException) {
            throw Exception("Email already in use. Please log in.")
        }
    }

    suspend fun loginWithGoogle(idToken: String): FirebaseUser =
        signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))

    suspend fun signupWithGoogle(idToken: String): FirebaseUser =
        signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))

    @Suppress("UNUSED_PARAMETER")
    suspend fun loginWithFacebook(accessToken: String): FirebaseUser {
        throw UnsupportedOperationException("Facebook sign-in disabled")
    }

    fun signOut() = auth.signOut()

    val currentUser: FirebaseUser? get() = auth.currentUser
}
