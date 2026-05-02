package ai.lufious.app

import ai.lufious.app.core.theme.Background
import ai.lufious.app.core.theme.LufiousTheme
import ai.lufious.app.core.theme.PrimaryColor
import ai.lufious.app.navgraph.AppNavHost
import ai.lufious.app.presentation.splash.viewmodel.SplashEvent
import ai.lufious.app.presentation.splash.viewmodel.SplashViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        splash.setKeepOnScreenCondition {
            splashViewModel.startRoute.value == null
        }
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(androidx.core.view.WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        enableEdgeToEdge()

        // Trigger auth check before setContent so the OS splash holds while we resolve the route.
        splashViewModel.send(SplashEvent.CheckAuth)

        setContent {
            val startRoute by splashViewModel.startRoute.collectAsState()

            LufiousTheme {
                Scaffold { _ ->
                    Box(modifier = Modifier.fillMaxSize().background(Background)) {
                        when (val route = startRoute) {
                            null -> {
                                // OS splash should still be on top; render a themed spinner as a fallback.
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = PrimaryColor)
                                }
                            }
                            else -> {
                                val navController = rememberNavController()
                                AppNavHost(
                                    navController = navController,
                                    launchGoogleIntent = { },
                                    launchFacebookIntent = { },
                                    startDestination = route
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
