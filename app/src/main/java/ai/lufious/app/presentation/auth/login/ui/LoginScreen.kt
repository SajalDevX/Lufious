@file:Suppress("DEPRECATION")
@file:OptIn(ExperimentalMaterial3Api::class)

package ai.lufious.app.presentation.auth.login.ui

import ai.lufious.app.R
import ai.lufious.app.core.theme.ClashDisplay
import ai.lufious.app.core.theme.LeafGreen
import ai.lufious.app.core.theme.PrimaryColor
import ai.lufious.app.core.theme.TextPrimary
import ai.lufious.app.core.utils.AUTH_GRAPH
import ai.lufious.app.core.utils.LaunchFacebookSignIn
import ai.lufious.app.core.utils.LaunchGoogleSignIn
import ai.lufious.app.core.utils.MAIN_GRAPH
import ai.lufious.app.core.utils.Screen
import ai.lufious.app.core.utils.UiEffect
import ai.lufious.app.core.utils.rememberResponsiveDimensions
import ai.lufious.app.presentation.auth.login.viewmodel.LoginEvent
import ai.lufious.app.presentation.auth.login.viewmodel.LoginViewModel
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.flow.collectLatest

private val PageBgTop = Color(0xFFF4FBF5)
private val PageBgMid = Color(0xFFE7F4E9)
private val PageBgBottom = Color(0xFFD6EAD9)
private val HeroGlowOne = Color(0x4D8BDBA6)
private val HeroGlowTwo = Color(0x3386C89A)
private val SheetCardTop = Color(0xFF0E3422)
private val SheetCardBottom = Color(0xFF18462D)
private val SheetCardBorder = Color(0x33FFFFFF)
private val HandleColor = Color(0x59FFFFFF)
private val DividerColor = Color(0x33FFFFFF)
private val SubtitleOnDark = Color(0xBFFFFFFF)
private val WhiteButtonBorder = Color(0x1F0A1A0F)
private val LinkMuted = Color(0x99FFFFFF)

@Composable
fun LoginPage(
    navController: NavController,
    launchGoogleIntent: () -> Unit,
    launchFacebookIntent: () -> Unit
) {
    LoginScreen(
        onEmailLogin = { navController.navigate(Screen.EmailLogin.route) },
        onSignUp = {
            navController.navigate(Screen.GetStarted.route) {
                popUpTo(Screen.GetStarted.route) { inclusive = false }
                launchSingleTop = true
            }
        },
        onBack = { navController.popBackStack() },
        navigateToHome = {
            navController.navigate(MAIN_GRAPH) {
                popUpTo(AUTH_GRAPH) { inclusive = true }
                launchSingleTop = true
            }
        },
        launchGoogleSignIn = launchGoogleIntent,
        launchFacebookSignIn = launchFacebookIntent
    )
}

@Composable
fun LoginScreen(
    onEmailLogin: () -> Unit,
    onSignUp: () -> Unit,
    onBack: () -> Unit,
    navigateToHome: () -> Unit,
    @Suppress("UNUSED_PARAMETER") launchGoogleSignIn: () -> Unit,
    launchFacebookSignIn: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val dims = rememberResponsiveDimensions()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestIdToken(stringResource(R.string.default_web_client_id))
        .build()
    val googleClient = GoogleSignIn.getClient(context, gso)

    val googleLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val token = runCatching {
            GoogleSignIn.getSignedInAccountFromIntent(result.data)
                .getResult(ApiException::class.java)
                ?.idToken
        }.getOrNull()
        viewModel.onEvent(LoginEvent.GoogleSignInResult(token ?: ""))
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is UiEffect.Navigate -> {
                    Toast.makeText(context, "Welcome back!", Toast.LENGTH_SHORT).show()
                    if (effect.route == "home") navigateToHome()
                }

                is UiEffect.ShowError -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
                }

                is LaunchGoogleSignIn -> {
                    googleClient.signOut().addOnCompleteListener {
                        googleLauncher.launch(googleClient.signInIntent)
                    }
                }

                is LaunchFacebookSignIn -> launchFacebookSignIn()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(PageBgTop, PageBgMid, PageBgBottom)
                )
            )
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(dims.widthFraction(0.62f).dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(HeroGlowOne, Color.Transparent)
                    )
                )
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(dims.widthFraction(0.56f).dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(HeroGlowTwo, Color.Transparent)
                    )
                )
        )

        Image(
            painter = painterResource(R.drawable.app_logo),
            contentDescription = "Lufious logo",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(top = dims.hR(12f).dp, end = dims.wR(20f).dp)
                .size(dims.R(44f).dp)
        )

        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(start = dims.wR(20f).dp, top = dims.hR(48f).dp)
        ) {
            Text(
                text = "Because Your",
                style = TextStyle(
                    fontFamily = ClashDisplay,
                    fontWeight = FontWeight.Bold,
                    fontSize = dims.R(38f).sp,
                    color = Color(0xFF4E5B52),
                    lineHeight = dims.R(44f).sp
                )
            )
            Text(
                text = "Plants Deserve It",
                style = TextStyle(
                    fontFamily = ClashDisplay,
                    fontWeight = FontWeight.Bold,
                    fontSize = dims.R(34f).sp,
                    color = LeafGreen,
                    lineHeight = dims.R(40f).sp
                )
            )
        }

        Image(
            painter = painterResource(R.drawable.toonping_login),
            contentDescription = "Mascot",
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = dims.hR(80f).dp)
                .size(dims.heightFraction(0.38f).dp)
        )
    }

    ModalBottomSheet(
        onDismissRequest = onBack,
        sheetState = sheetState,
        containerColor = Color.Transparent,
        scrimColor = Color(0x52000000),
        tonalElevation = 0.dp,
        shape = RoundedCornerShape(topStart = 34.dp, topEnd = 34.dp),
        windowInsets = WindowInsets(0),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 6.dp)
                    .width(44.dp)
                    .height(4.dp)
                    .background(HandleColor, RoundedCornerShape(100.dp))
            )
        }
    ) {
        LoginSheetContent(
            dims = dims,
            onGoogleLogin = { viewModel.onEvent(LoginEvent.GoogleSignInClicked) },
            onEmailLogin = onEmailLogin,
            onSignUp = onSignUp
        )
    }
}

@Composable
private fun LoginSheetContent(
    dims: ai.lufious.app.core.utils.ResponsiveDimensions,
    onGoogleLogin: () -> Unit,
    onEmailLogin: () -> Unit,
    onSignUp: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(
                start = dims.wR(16f).dp,
                end = dims.wR(16f).dp,
                bottom = dims.hR(12f).dp
            )
            .clip(RoundedCornerShape(dims.R(28f).dp))
            .background(
                Brush.verticalGradient(
                    listOf(SheetCardTop, SheetCardBottom)
                )
            )
            .border(1.dp, SheetCardBorder, RoundedCornerShape(dims.R(28f).dp))
            .padding(horizontal = dims.wR(22f).dp, vertical = dims.hR(18f).dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(Color(0x1FFFFFFF))
                .padding(horizontal = dims.wR(12f).dp, vertical = dims.hR(4f).dp)
        ) {
            Text(
                text = "Welcome Back",
                style = TextStyle(
                    fontFamily = ClashDisplay,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = dims.R(12f).sp,
                    color = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.height(dims.hR(10f).dp))

        Text(
            text = "Log back into your Lufious account",
            style = TextStyle(
                fontFamily = ClashDisplay,
                fontWeight = FontWeight.Normal,
                fontSize = dims.R(14f).sp,
                color = SubtitleOnDark
            )
        )

        Spacer(modifier = Modifier.height(dims.hR(22f).dp))

        OutlinedButton(
            onClick = onGoogleLogin,
            modifier = Modifier
                .fillMaxWidth()
                .height(dims.hR(54f).dp),
            shape = RoundedCornerShape(dims.R(16f).dp),
            border = BorderStroke(1.dp, WhiteButtonBorder),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.White,
                contentColor = TextPrimary
            )
        ) {
            Image(
                painter = painterResource(R.drawable.ic_google),
                contentDescription = null,
                modifier = Modifier.size(dims.R(20f).dp)
            )
            Spacer(modifier = Modifier.width(dims.wR(10f).dp))
            Text(
                text = "Continue with Google",
                style = TextStyle(
                    fontFamily = ClashDisplay,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = dims.R(15f).sp,
                    color = TextPrimary
                )
            )
        }

        Spacer(modifier = Modifier.height(dims.hR(12f).dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = DividerColor)
            Text(
                text = "  or continue with  ",
                style = TextStyle(
                    fontFamily = ClashDisplay,
                    fontWeight = FontWeight.Normal,
                    fontSize = dims.R(12f).sp,
                    color = SubtitleOnDark
                )
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = DividerColor)
        }

        Spacer(modifier = Modifier.height(dims.hR(12f).dp))

        Button(
            onClick = onEmailLogin,
            modifier = Modifier
                .fillMaxWidth()
                .height(dims.hR(54f).dp),
            shape = RoundedCornerShape(dims.R(16f).dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = LeafGreen,
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 1.dp
            )
        ) {
            Image(
                painter = painterResource(R.drawable.ic_email),
                contentDescription = null,
                modifier = Modifier.size(dims.R(20f).dp)
            )
            Spacer(modifier = Modifier.width(dims.wR(10f).dp))
            Text(
                text = "Continue with Email",
                style = TextStyle(
                    fontFamily = ClashDisplay,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = dims.R(15f).sp
                )
            )
        }

        Spacer(modifier = Modifier.height(dims.hR(18f).dp))

        Row(horizontalArrangement = Arrangement.Center) {
            Text(
                text = "Don't have an account?  ",
                style = TextStyle(
                    fontFamily = ClashDisplay,
                    fontWeight = FontWeight.Normal,
                    fontSize = dims.R(13f).sp,
                    color = LinkMuted
                )
            )
            Text(
                text = "Sign up",
                style = TextStyle(
                    fontFamily = ClashDisplay,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = dims.R(13f).sp,
                    color = LeafGreen
                ),
                modifier = Modifier.clickable { onSignUp() }
            )
        }

        Spacer(modifier = Modifier.height(dims.hR(4f).dp))
    }
}
