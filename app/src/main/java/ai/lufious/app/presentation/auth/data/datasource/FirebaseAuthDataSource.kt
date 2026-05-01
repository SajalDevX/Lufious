package ai.lufious.app.presentation.auth.data.datasource

import ai.lufious.app.core.firebase.FirestoreManager
import ai.lufious.app.core.firebase.utils.FirestoreModels
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseAuthDataSource @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestoreManager: FirestoreManager
) {
    private suspend fun signInWithCredential(cred: AuthCredential): FirebaseUser =
        suspendCoroutine { cont ->
            auth.signInWithCredential(cred)
                .addOnSuccessListener { cont.resume(it.user!!) }
                .addOnFailureListener { cont.resumeWithException(it) }
        }

    suspend fun loginWithEmail(email: String, pw: String): FirebaseUser {
        // Check if email exists in Firestore
        val emailExistsInFirestore = firestoreManager.checkIfDocumentExists(FirestoreModels.User.collection, FirestoreModels.User.EMAIL, email)
        if (!emailExistsInFirestore) {
            throw Exception("Account does not exist. Please sign up.")
        }

        return suspendCoroutine { cont ->
            auth.signInWithEmailAndPassword(email, pw)
                .addOnSuccessListener { cont.resume(it.user!!) }
                .addOnFailureListener { cont.resumeWithException(it) }
        }
    }

    suspend fun signupWithEmail(email: String, pw: String): FirebaseUser {
        // Check if email already exists in Firestore
        val emailExistsInFirestore = firestoreManager.checkIfDocumentExists(FirestoreModels.User.collection, FirestoreModels.User.EMAIL, email)
        if (emailExistsInFirestore) {
            throw Exception("Email already in use. Please log in.")
        }

        return suspendCoroutine { cont ->
            auth.createUserWithEmailAndPassword(email, pw)
                .addOnSuccessListener { cont.resume(it.user!!) }
                .addOnFailureListener { cont.resumeWithException(it) }
        }
    }

    suspend fun loginWithGoogle(idToken: String): FirebaseUser {
        val cred = GoogleAuthProvider.getCredential(idToken, null)
        val googleEmail = getEmailFromCredential(cred)

        // Check if email exists in Firestore
        val emailExistsInFirestore = firestoreManager.checkIfDocumentExists("users", "email", googleEmail)
        if (!emailExistsInFirestore) {
            throw Exception("No account found for this Google email. Please sign up first.")
        }

        return signInWithCredential(cred)
    }
    suspend fun signupWithGoogle(idToken: String): FirebaseUser {
        val cred = GoogleAuthProvider.getCredential(idToken, null)
        val googleEmail = getEmailFromCredential(cred)

        // Check if email already exists in Firestore
        val emailExistsInFirestore = firestoreManager.checkIfDocumentExists("users", "email", googleEmail)
        if (emailExistsInFirestore) {
            throw Exception("Email already in use. Please log in.")
        }

        return signInWithCredential(cred)
    }
    private suspend fun getEmailFromCredential(cred: AuthCredential): String =
        suspendCoroutine { cont ->
            auth.signInWithCredential(cred)
                .addOnSuccessListener { task ->
                    val email = task.user?.email
                    // Immediately sign out and return email
                    auth.currentUser?.delete()
                    if (email != null) cont.resume(email)
                    else cont.resumeWithException(Exception("Failed to extract email from Google credential"))
                }
                .addOnFailureListener { cont.resumeWithException(it) }
        }

    suspend fun loginWithFacebook(accessToken: String): FirebaseUser {
        val cred = FacebookAuthProvider.getCredential(accessToken)
        return signInWithCredential(cred)
    }

    fun signOut() = auth.signOut()

    val currentUser: FirebaseUser? get() = auth.currentUser
}