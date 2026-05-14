package ai.lufious.app.core.firebase

import ai.lufious.app.core.network.LufiousApi
import ai.lufious.app.core.network.dto.FcmTokenRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LufiousMessagingService : FirebaseMessagingService() {

    @Inject lateinit var api: LufiousApi

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        if (FirebaseAuth.getInstance().currentUser == null) return
        scope.launch {
            runCatching { api.registerFcmToken(FcmTokenRequest(token)) }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
    }
}
