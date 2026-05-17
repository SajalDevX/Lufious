package ai.lufious.app.presentation.scan.ui

import ai.lufious.app.core.theme.TextPrimary
import ai.lufious.app.presentation.scan.data.models.AiChatMessageModel
import ai.lufious.app.presentation.scan.viewmodel.AiChatEvent
import ai.lufious.app.presentation.scan.viewmodel.AiChatState
import ai.lufious.app.presentation.scan.viewmodel.AiChatViewModel
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

private val ChatBg = Color(0xFFF8FBF8)
private val Brand = Color(0xFF1A5C35)
private val AssistantBubble = Color.White
private val UserBubble = Color(0xFF1A5C35)
private val ChipBg = Color(0xFFE8F5E8)
private val Hint = Color(0xFF7A8C82)
private val Border = Color(0xFFDCE5D5)

@Composable
fun AiChatScreen(
    navController: NavController,
    viewModel: AiChatViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()

    // Auto-scroll when a new message arrives
    LaunchedEffect(state.messages.size, state.isReplying) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size - 1 + if (state.isReplying) 1 else 0)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ChatBg)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            ChatHeader(
                species = state.speciesName.ifBlank { "Plant assistant" },
                onBack = { navController.popBackStack() }
            )

            // Show full-screen loader until the seed message lands.
            if (state.messages.isEmpty() && !state.isReplying) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Brand, strokeWidth = 3.dp)
                        Spacer(Modifier.height(14.dp))
                        Text(
                            text = "Analysing your plant…",
                            color = TextPrimary.copy(alpha = 0.7f),
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.messages, key = { it.timestamp }) { msg ->
                        MessageBubble(msg)
                    }
                    if (state.isReplying) {
                        item { TypingBubble() }
                    }
                }
            }

            ChatComposer(
                value = state.inputText,
                enabled = !state.isReplying && state.messages.isNotEmpty(),
                onValueChange = { viewModel.onEvent(AiChatEvent.InputChanged(it)) },
                onSend = { viewModel.onEvent(AiChatEvent.Send) }
            )
        }
    }
}

@Composable
private fun ChatHeader(species: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .statusBarsPadding()
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(ChatBg)
                .clickable { onBack() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = TextPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = species,
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1
            )
            Text(
                text = "AI plant assistant",
                color = TextPrimary.copy(alpha = 0.55f),
                fontSize = 11.sp
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(ChipBg)
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(
                text = "● Online",
                color = Brand,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun MessageBubble(msg: AiChatMessageModel) {
    val isUser = msg.role == "user"
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = if (isUser) 18.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 18.dp
                    )
                )
                .background(if (isUser) UserBubble else AssistantBubble)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = msg.content,
                color = if (isUser) Color.White else TextPrimary,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun TypingBubble() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Row(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = 4.dp,
                        bottomEnd = 18.dp
                    )
                )
                .background(AssistantBubble)
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            TypingDot(); TypingDot(); TypingDot()
        }
    }
}

@Composable
private fun TypingDot() {
    Box(
        modifier = Modifier
            .size(6.dp)
            .clip(CircleShape)
            .background(TextPrimary.copy(alpha = 0.4f))
    )
}

@Composable
private fun ChatComposer(
    value: String,
    enabled: Boolean,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text("Ask about this plant…", color = Hint, fontSize = 14.sp) },
            modifier = Modifier.weight(1f),
            enabled = enabled,
            shape = RoundedCornerShape(24.dp),
            maxLines = 4,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Brand,
                unfocusedBorderColor = Border,
                focusedContainerColor = ChatBg,
                unfocusedContainerColor = ChatBg,
                disabledContainerColor = ChatBg,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )
        Spacer(Modifier.width(8.dp))
        val canSend = enabled && value.isNotBlank()
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(if (canSend) Brand else Brand.copy(alpha = 0.3f))
                .clickable(enabled = canSend) { onSend() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
