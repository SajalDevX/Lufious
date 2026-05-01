package ai.lufious.app.core.firebase

import com.google.firebase.storage.FirebaseStorage
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.tasks.await

@Singleton
class StorageManager @Inject constructor(
    private val storage: FirebaseStorage
) {

    suspend fun uploadPlantPhoto(uid: String, plantId: String, bytes: ByteArray): String {
        val ref = storage.reference
            .child("users/$uid/plants/$plantId/${System.currentTimeMillis()}.jpg")
        ref.putBytes(bytes).await()
        return ref.downloadUrl.await().toString()
    }

    suspend fun uploadScanPhoto(uid: String, scanId: String, bytes: ByteArray): String {
        val ref = storage.reference
            .child("users/$uid/scans/$scanId/${System.currentTimeMillis()}.jpg")
        ref.putBytes(bytes).await()
        return ref.downloadUrl.await().toString()
    }

    suspend fun uploadListingPhoto(uid: String, listingId: String, bytes: ByteArray): String {
        val ref = storage.reference
            .child("listings/$uid/$listingId/${System.currentTimeMillis()}.jpg")
        ref.putBytes(bytes).await()
        return ref.downloadUrl.await().toString()
    }

    suspend fun uploadProfilePhoto(uid: String, bytes: ByteArray): String {
        val ref = storage.reference.child("users/$uid/profile.jpg")
        ref.putBytes(bytes).await()
        return ref.downloadUrl.await().toString()
    }
}
