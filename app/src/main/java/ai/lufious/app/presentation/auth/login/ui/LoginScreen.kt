@file:Suppress("DEPRECATION")
@file:OptIn(ExperimentalMaterial3Api::class)

package ai.lufious.app.presentation.auth.login.ui

import ai.lufious.app.R
import ai.lufious.app.core.theme.ClashDisplay
import ai.lufious.app.core.theme.LeafGreen
import ai.lufious.app.core.theme.PrimaryColor
import ai.lufious.app.core.theme.TextPrimary
import ai.lufious.app.core.utils.LaunchFacebookSignIn
import ai.lufious.app.core.utils.LaunchGoogleSignIn
import ai.lufious.app.core.utils.Screen
import ai.lufious.app.core.utils.UiEffect
import ai.lufious.app.core.utils.AUTH_GRAPH
import ai.lufious.app.core.utils.MAIN_GRAPH
import ai.lufious.app.core.utils.rememberResponsiveDimensions
import ai.lufious.app.presentation.auth.login.viewmodel.LoginEvent
import ai.lufious.app.presentation.auth.login.viewmodel.LoginViewModel
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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

private val PageBg       = Color(0xFFF0FAF2)
private val SheetDarkBg  = Color(0xFF0D3320)   // dark forest green
private val HandleColor  = Color(0x33FFFFFF)
private val DividerColor = Color(0x33FFFFFF)
private val SubtitleOnDark = Color(0xAAFFFFFF)
private val GoogleBtnBg  = Color(0xFFFFFFFF)
private val EmailBtnBg   = Color(0x1AFFFFFF)
private val EmailBtnBorder = Color(0x66FFFFFF)

@Composable
fun LoginPage(
    navController: NavController,
    launchGoogleIntent: () -> Unit,
    launchFacebookIntent: () -> Unit
) {
    LoginScreen(
        onEmailLogin = { navController.navigate(Screen.EmailLogin.route) },
        onSignUp = { navController.navigate(Screen.GetStarted.route) },
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
    val context = LocalContext.current
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

    // Background — fills edge-to-edge; individual items apply their own inset padding
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBg)
    ) {
        // Logo top-right (status bar aware)
        Image(
            painter = painterResource(R.drawable.app_logo),
            contentDescription = "Lufious logo",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(top = dims.hR(12f).dp, end = dims.wR(20f).dp)
                .size(dims.R(44f).dp)
        )

        // Headline top-left (status bar aware)
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

        // Mascot centered above sheet
        Image(
            painter = painterResource(R.drawable.toonping_login),
            contentDescription = "Mascot",
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = dims.hR(80f).dp)
                .size(dims.heightFraction(0.38f).dp)
        )
    }

    // Dark green bottom sheet; windowInsets zeroed so only sheet content pads for nav bar
    ModalBottomSheet(
        onDismissRequest = onBack,
        sheetState = sheetState,
        containerColor = SheetDarkBg,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        windowInsets = WindowInsets(0),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 4.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .background(HandleColor, RoundedCornerShape(50))
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
            .padding(horizontal = dims.wR(24f).dp)
            .padding(top = dims.hR(8f).dp, bottom = dims.hR(24f).dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome Back",
            style = TextStyle(
                fontFamily = ClashDisplay,
                fontWeight = FontWeight.Bold,
                fontSize = dims.R(22f).sp,
                color = Color.White
            )
        )

        Spacer(modifier = Modifier.height(dims.hR(6f).dp))

        Text(
            text = "Log back into your Lufious account",
            style = TextStyle(
                fontFamily = ClashDisplay,
                fontWeight = FontWeight.Normal,
                fontSize = dims.R(13f).sp,
                color = SubtitleOnDark
            )
        )

        Spacer(modifier = Modifier.height(dims.hR(24f).dp))

        // Google button — white on dark green
        OutlinedButton(
            onClick = onGoogleLogin,
            modifier = Modifier
                .fillMaxWidth()
                .height(dims.hR(52f).dp),
            shape = RoundedCornerShape(dims.R(14f).dp),
            border = BorderStroke(0.dp, Color.Transparent),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = GoogleBtnBg
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
                    fontWeight = FontWeight.Medium,
                    fontSize = dims.R(15f).sp,
                    color = TextPrimary
                )
            )
        }

        Spacer(modifier = Modifier.height(dims.hR(14f).dp))

        // Divider with "or"
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = DividerColor)
            Text(
                text = "  or  ",
                style = TextStyle(
                    fontFamily = ClashDisplay,
                    fontWeight = FontWeight.Normal,
                    fontSize = dims.R(13f).sp,
                    color = SubtitleOnDark
                )
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = DividerColor)
        }

        Spacer(modifier = Modifier.height(dims.hR(14f).dp))

        // Email button — semi-transparent on dark green
        OutlinedButton(
            onClick = onEmailLogin,
            modifier = Modifier
                .fillMaxWidth()
                .height(dims.hR(52f).dp),
            shape = RoundedCornerShape(dims.R(14f).dp),
            border = BorderStroke(1.dp, EmailBtnBorder),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = EmailBtnBg
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
                    fontWeight = FontWeight.Medium,
                    fontSize = dims.R(15f).sp,
                    color = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.height(dims.hR(24f).dp))

        // Sign-up link
        Row(horizontalArrangement = Arrangement.Center) {
            Text(
                text = "Don't have an account?  ",
                style = TextStyle(
                    fontFamily = ClashDisplay,
                    fontWeight = FontWeight.Normal,
                    fontSize = dims.R(13f).sp,
                    color = SubtitleOnDark
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

        Spacer(modifier = Modifier.height(dims.hR(8f).dp))
    }
}
