package ai.lufious.app.core.firebase

import ai.lufious.app.core.firebase.utils.FirestoreModels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class LufiousMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance()
            .collection(FirestoreModels.User.collection)
            .document(uid)
            .set(
                mapOf(
                    FirestoreModels.User.FCM_TOKEN to token,
                    FirestoreModels.User.UPDATED_AT to System.currentTimeMillis()
                ),
                SetOptions.merge()
            )
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
    }
}
