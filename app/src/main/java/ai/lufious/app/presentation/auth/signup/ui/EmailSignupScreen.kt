@file:Suppress("DEPRECATION")
@file:OptIn(ExperimentalMaterial3Api::class)

package ai.lufious.app.presentation.auth.signup.ui

import ai.lufious.app.R
import ai.lufious.app.core.theme.ClashDisplay
import ai.lufious.app.core.theme.LeafGreen
import ai.lufious.app.core.theme.TextPrimary
import ai.lufious.app.core.theme.TextSecondary
import ai.lufious.app.core.utils.UiEffect
import ai.lufious.app.core.utils.ValidationResult
import ai.lufious.app.core.utils.rememberResponsiveDimensions
import ai.lufious.app.presentation.auth.signup.viewmodel.SignupEvent
import ai.lufious.app.presentation.auth.signup.viewmodel.SignupViewModel
import ai.lufious.app.presentation.utils.CommonTextField
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
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
private val FieldBg = Color(0x1AFFFFFF)
private val FieldBorder = Color(0x59FFFFFF)
private val FieldHint = Color(0xB3FFFFFF)
private val DangerText = Color(0xFFFFB4B4)
private val LinkMuted = Color(0x99FFFFFF)

@Composable
fun EmailSignupScreen(
    viewModel: SignupViewModel = hiltViewModel(),
    onNavigate: (String) -> Unit,
    onLogin: () -> Unit,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val dims = rememberResponsiveDimensions()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is UiEffect.ShowError -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }

                is UiEffect.Navigate -> {
                    onNavigate(effect.route)
                }

                else -> Unit
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

        androidx.compose.foundation.Image(
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
                text = "Continue with",
                style = TextStyle(
                    fontFamily = ClashDisplay,
                    fontWeight = FontWeight.Bold,
                    fontSize = dims.R(36f).sp,
                    color = Color(0xFF4E5B52),
                    lineHeight = dims.R(42f).sp
                )
            )
            Text(
                text = "Email",
                style = TextStyle(
                    fontFamily = ClashDisplay,
                    fontWeight = FontWeight.Bold,
                    fontSize = dims.R(34f).sp,
                    color = LeafGreen,
                    lineHeight = dims.R(40f).sp
                )
            )
        }

        androidx.compose.foundation.Image(
            painter = painterResource(R.drawable.toonping_signup),
            contentDescription = "Mascot",
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = dims.hR(100f).dp)
                .size(dims.heightFraction(0.35f).dp)
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
        EmailSignupSheet(
            dims = dims,
            state = state,
            onSubmit = { viewModel.onEvent(SignupEvent.Submit) },
            onEmailChange = { viewModel.onEvent(SignupEvent.EmailChanged(it)) },
            onPasswordChange = { viewModel.onEvent(SignupEvent.PasswordChanged(it)) },
            onLogin = onLogin
        )
    }
}

@Composable
private fun EmailSignupSheet(
    dims: ai.lufious.app.core.utils.ResponsiveDimensions,
    state: ai.lufious.app.presentation.auth.signup.viewmodel.SignupState,
    onSubmit: () -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.74f)
            .verticalScroll(rememberScrollState())
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
            .padding(horizontal = dims.wR(20f).dp, vertical = dims.hR(18f).dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(Color(0x1FFFFFFF))
                .padding(horizontal = dims.wR(12f).dp, vertical = dims.hR(4f).dp)
        ) {
            Text(
                text = "Continue with Email",
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
            text = "Create Your Account",
            style = TextStyle(
                fontFamily = ClashDisplay,
                fontWeight = FontWeight.Bold,
                fontSize = dims.R(22f).sp,
                color = Color.White
            )
        )

        Spacer(modifier = Modifier.height(dims.hR(6f).dp))

        Text(
            text = "Set your email and password to join Lufious.",
            style = TextStyle(
                fontFamily = ClashDisplay,
                fontWeight = FontWeight.Normal,
                fontSize = dims.R(13f).sp,
                color = SubtitleOnDark
            )
        )

        Spacer(modifier = Modifier.height(dims.hR(14f).dp))
        HorizontalDivider(color = DividerColor)
        Spacer(modifier = Modifier.height(dims.hR(14f).dp))

        Text(
            text = "Email Address",
            style = TextStyle(
                fontFamily = ClashDisplay,
                fontWeight = FontWeight.SemiBold,
                fontSize = dims.R(13f).sp,
                color = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(dims.hR(6f).dp))

        CommonTextField(
            value = state.email,
            onValueChange = onEmailChange,
            responsive = dims,
            placeholder = "you@example.com",
            imeAction = ImeAction.Next,
            onImeAction = { },
            isError = state.emailValidation is ValidationResult.Invalid,
            backgroundColor = FieldBg,
            borderColor = FieldBorder,
            placeholderColor = FieldHint,
            textColor = Color.White,
            cursorColor = Color.White,
            textStyle = TextStyle(
                fontFamily = ClashDisplay,
                fontWeight = FontWeight.Medium,
                fontSize = dims.R(14f).sp
            )
        )

        Spacer(modifier = Modifier.height(dims.hR(6f).dp))

        Text(
            text = "We'll use this email to create and access your account.",
            style = TextStyle(
                fontFamily = ClashDisplay,
                fontWeight = FontWeight.Normal,
                fontSize = dims.R(11f).sp,
                color = SubtitleOnDark
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(dims.hR(2f).dp))

        val emailError = (state.emailValidation as? ValidationResult.Invalid)?.reason
        if (emailError != null) {
            Text(
                text = emailError,
                color = DangerText,
                style = TextStyle(
                    fontFamily = ClashDisplay,
                    fontWeight = FontWeight.Normal,
                    fontSize = dims.R(11f).sp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dims.hR(4f).dp)
            )
        }

        Spacer(modifier = Modifier.height(dims.hR(10f).dp))

        Text(
            text = "Password",
            style = TextStyle(
                fontFamily = ClashDisplay,
                fontWeight = FontWeight.SemiBold,
                fontSize = dims.R(13f).sp,
                color = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(dims.hR(6f).dp))

        CommonTextField(
            value = state.password,
            onValueChange = onPasswordChange,
            responsive = dims,
            placeholder = "Create a secure password",
            isPassword = true,
            imeAction = ImeAction.Done,
            onImeAction = onSubmit,
            isError = state.passwordValidation is ValidationResult.Invalid,
            backgroundColor = FieldBg,
            borderColor = FieldBorder,
            placeholderColor = FieldHint,
            textColor = Color.White,
            cursorColor = Color.White,
            textStyle = TextStyle(
                fontFamily = ClashDisplay,
                fontWeight = FontWeight.Medium,
                fontSize = dims.R(14f).sp
            )
        )

        Spacer(modifier = Modifier.height(dims.hR(6f).dp))

        Text(
            text = "Choose a strong password with at least 8 characters.",
            style = TextStyle(
                fontFamily = ClashDisplay,
                fontWeight = FontWeight.Normal,
                fontSize = dims.R(11f).sp,
                color = SubtitleOnDark
            ),
            modifier = Modifier.fillMaxWidth()
        )

        val passwordError = (state.passwordValidation as? ValidationResult.Invalid)?.reason
        if (passwordError != null) {
            Text(
                text = passwordError,
                color = DangerText,
                style = TextStyle(
                    fontFamily = ClashDisplay,
                    fontWeight = FontWeight.Normal,
                    fontSize = dims.R(11f).sp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dims.hR(4f).dp)
            )
        }

        Spacer(modifier = Modifier.height(dims.hR(14f).dp))

        Button(
            onClick = onSubmit,
            enabled = state.isSubmitEnabled && !state.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(dims.hR(52f).dp),
            shape = RoundedCornerShape(dims.R(16f).dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = LeafGreen,
                disabledContainerColor = LeafGreen.copy(alpha = 0.45f)
            )
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(dims.R(18f).dp)
                )
            } else {
                Text(
                    text = "Create Account",
                    style = TextStyle(
                        fontFamily = ClashDisplay,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = dims.R(15f).sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(dims.hR(12f).dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Already have an account? ",
                style = TextStyle(
                    fontFamily = ClashDisplay,
                    fontWeight = FontWeight.Normal,
                    fontSize = dims.R(12f).sp,
                    color = LinkMuted
                )
            )
            Text(
                text = "Log in",
                style = TextStyle(
                    fontFamily = ClashDisplay,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = dims.R(12f).sp,
                    color = LeafGreen
                ),
                modifier = Modifier.clickable { onLogin() }
            )
        }

        Spacer(modifier = Modifier.height(dims.hR(2f).dp))
    }
}
