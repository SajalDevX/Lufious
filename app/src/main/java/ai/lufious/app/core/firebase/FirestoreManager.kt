package ai.lufious.app.core.firebase

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreManager @Inject constructor(
    private val db: FirebaseFirestore
) {

    // Generic method to fetch a document by ID from any collection
    suspend fun <T> getDocument(collection: String, documentId: String, clazz: Class<T>): T? {
        return try {
            val documentSnapshot = db.collection(collection).document(documentId).get().await()
            documentSnapshot.toObject(clazz)
        } catch (e: Exception) {
            throw Exception("Failed to fetch document in Firestore: ${e.message}") // In case of error or document not found, return null
        }
    }

    // Generic method to check if a document exists by a field value
    suspend fun checkIfDocumentExists(collection: String, field: String, value: String): Boolean {
        return try {
            val querySnapshot = db.collection(collection).whereEqualTo(field, value).get().await()
            !querySnapshot.isEmpty
        } catch (e: Exception) {
            false // If error, assume the document doesn't exist
        }
    }

    // Add or update a document in Firestore
    suspend fun <T: Any> setDocument(collection: String, documentId: String, data: T) {
        try {
            db.collection(collection).document(documentId).set(data).await()
        } catch (e: Exception) {
            throw Exception("Failed to set document in Firestore: ${e.message}")
        }
    }

    // Delete a document from Firestore
    suspend fun deleteDocument(collection: String, documentId: String) {
        try {
            db.collection(collection).document(documentId).delete().await()
        } catch (e: Exception) {
            throw Exception("Failed to delete document: ${e.message}")
        }
    }
}
