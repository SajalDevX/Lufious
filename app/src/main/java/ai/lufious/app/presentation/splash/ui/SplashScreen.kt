package ai.lufious.app.presentation.splash.ui

import ai.lufious.app.core.utils.UiEffect
import ai.lufious.app.presentation.splash.viewmodel.SplashEvent
import ai.lufious.app.presentation.splash.viewmodel.SplashEvent.*
import ai.lufious.app.presentation.splash.viewmodel.SplashViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(key1 = true) {
        viewModel.send(SplashEvent.CheckAuth)
    }

    LaunchedEffect(key1 = viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is UiEffect.Navigate -> {
                    navController.navigate(effect.route) {
                        popUpTo("splash") { inclusive = true }
                        launchSingleTop = true
                    }
                }
                is UiEffect.ShowError -> {
                }

                else -> {}
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (state.isLoading) {
            CircularProgressIndicator()
        }
    }
}
