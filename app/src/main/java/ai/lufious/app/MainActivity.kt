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
import ai.lufious.app.presentation.splash.viewmodel.SplashEvent
import ai.lufious.app.presentation.splash.viewmodel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install and hold the native splash until isReady == true
        val splash = installSplashScreen()
        splash.setKeepOnScreenCondition {
            splashViewModel.isReady.value.not()
        }

        super.onCreate(savedInstanceState)
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
                        AppNavHost(navController = navController)
                    }
                }
            }
        }

        // Trigger your cache check
        splashViewModel.send(SplashEvent.CheckAuth)
    }
}
