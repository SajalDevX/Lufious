package ai.lufious.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import ai.lufious.app.core.theme.LufiousTheme
import ai.lufious.app.core.utils.UiEffect
import ai.lufious.app.navgraph.AppNavHost
import ai.lufious.app.presentation.auth.login.viewmodel.LoginEvent
import ai.lufious.app.presentation.auth.login.viewmodel.LoginViewModel
import ai.lufious.app.presentation.auth.utils.SocialAuthManager
import ai.lufious.app.presentation.splash.viewmodel.SplashEvent
import ai.lufious.app.presentation.splash.viewmodel.SplashViewModel
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import jakarta.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val splashViewModel: SplashViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var socialAuthManager: SocialAuthManager

    private lateinit var googleLauncher: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        splash.setKeepOnScreenCondition {
            splashViewModel.isReady.value.not()
        }
        super.onCreate(savedInstanceState)

        googleLauncher = socialAuthManager.setup(
            activity = this,
            googleWebClientId = getString(R.string.default_web_client_id),
            onGoogleToken = { idToken ->
                loginViewModel.onEvent(LoginEvent.GoogleSignInResult(idToken ?: ""))
            },
            onFacebookToken = null
        )



        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()

            LaunchedEffect(Unit) {
                splashViewModel.effects
                    .collectLatest { effect ->
                        if (effect is UiEffect.Navigate) {
                            navController.navigate(effect.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                    }
            }

            LufiousTheme {
                Scaffold { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AppNavHost(navController = navController,    launchGoogleIntent = { socialAuthManager.launchGoogle(googleLauncher) },
                            launchFacebookIntent = {  })
                    }
                }
            }
        }
        splashViewModel.send(SplashEvent.CheckAuth)
    }
    @Deprecated("onActivityResult is deprecated. Required here for Facebook SDK only.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        socialAuthManager.handleFacebookActivityResult(requestCode, resultCode, data)
    }

}
