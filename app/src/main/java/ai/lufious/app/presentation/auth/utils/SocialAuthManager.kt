package ai.lufious.app.presentation.auth.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
//import com.facebook.CallbackManager
//import com.facebook.FacebookCallback
//import com.facebook.FacebookException
//import com.facebook.login.LoginManager
//import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import jakarta.inject.Inject
import javax.inject.Singleton

@Singleton
class SocialAuthManager @Inject constructor() {
    private lateinit var googleClient: GoogleSignInClient
//    private lateinit var callbackManager: CallbackManager

    fun setup(
        activity: ComponentActivity,
        googleWebClientId: String,
        onGoogleToken: (String?) -> Unit,
        onFacebookToken: ((String?) -> Unit)? = null
    ): ActivityResultLauncher<Intent> {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(googleWebClientId)
            .requestEmail()
            .build()

        googleClient = GoogleSignIn.getClient(activity, gso)

//        callbackManager = CallbackManager.Factory.create()

        return activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val idToken = try {
                task.getResult(ApiException::class.java)?.idToken
            } catch (e: ApiException) {
                null
            }
            onGoogleToken(idToken)
        }
    }

    fun launchGoogle(launcher: ActivityResultLauncher<Intent>) {
        launcher.launch(googleClient.signInIntent)
    }

//    fun launchFacebook(activity: Activity) {
//        LoginManager.getInstance().logInWithReadPermissions(
//            activity,
//            listOf("email", "public_profile")
//        )
//    }
//
//    fun handleFacebookActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        callbackManager.onActivityResult(requestCode, resultCode, data)
//    }
}
