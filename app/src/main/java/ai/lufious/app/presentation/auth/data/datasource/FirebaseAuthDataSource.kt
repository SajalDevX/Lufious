package ai.lufious.app.presentation.auth.data.datasource

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import jakarta.inject.Inject
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
    suspend fun signupWithEmail(email: String, pw: String): FirebaseUser =
        suspendCoroutine { cont ->
            auth.createUserWithEmailAndPassword(email, pw)
                .addOnSuccessListener { cont.resume(it.user!!) }
                .addOnFailureListener { cont.resumeWithException(it) }
        }

    suspend fun loginWithGoogle(idToken: String): FirebaseUser {
        val cred = GoogleAuthProvider.getCredential(idToken, null)
        return signInWithCredential(cred)
    }

    suspend fun loginWithFacebook(accessToken: String): FirebaseUser {
        val cred = FacebookAuthProvider.getCredential(accessToken)
        return signInWithCredential(cred)
    }

    fun signOut() = auth.signOut()

    val currentUser: FirebaseUser? get() = auth.currentUser
}