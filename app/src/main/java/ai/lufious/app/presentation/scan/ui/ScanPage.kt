package ai.lufious.app.presentation.scan.ui

import ai.lufious.app.R
import ai.lufious.app.core.theme.ClashDisplay
import ai.lufious.app.core.theme.Background
import ai.lufious.app.core.theme.PrimaryColor
import ai.lufious.app.core.theme.TextPrimary
import ai.lufious.app.core.utils.UiEffect
import ai.lufious.app.presentation.scan.data.models.AgentKey
import ai.lufious.app.presentation.scan.viewmodel.ScanEvent
import ai.lufious.app.presentation.scan.viewmodel.ScanViewModel
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.Text as Text3
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.LaunchedEffect as LE
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.concurrent.futures.await
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanPage(
    navController: NavController,
    outerNavController: NavController = navController,
    modifier: Modifier = Modifier,
    viewModel: ScanViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is UiEffect.Navigate -> outerNavController.navigate(effect.route)
                is UiEffect.ShowError ->
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                else -> {}
            }
        }
    }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    var showCamera by remember { mutableStateOf(false) }
    var showPickerSheet by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
        if (granted) showCamera = true
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            }.getOrNull()?.let { bytes ->
                viewModel.onEvent(ScanEvent.Scan(bytes, state.selectedAgent))
            }
        }
    }

    val openCamera = {
        showPickerSheet = false
        if (hasCameraPermission) showCamera = true
        else permissionLauncher.launch(Manifest.permission.CAMERA)
    }
    val openGallery = {
        showPickerSheet = false
        galleryLauncher.launch(
            androidx.activity.result.PickVisualMediaRequest(
                ActivityResultContracts.PickVisualMedia.ImageOnly
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("scan_screen")
    ) {
        when {
            !showCamera -> AgentGridContent(
                onAgentSelected = { agent ->
                    viewModel.onEvent(ScanEvent.SelectAgent(agent))
                    showPickerSheet = true
                }
            )
            hasCameraPermission -> CameraContent(
                isScanning = state.isScanning,
                onClose = { showCamera = false },
                onScan = { imageCapture ->
                    val agent = state.selectedAgent
                    imageCapture.takePicture(
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageCapturedCallback() {
                            override fun onCaptureSuccess(image: ImageProxy) {
                                val buffer = image.planes[0].buffer
                                val bytes = ByteArray(buffer.remaining())
                                buffer.get(bytes)
                                image.close()
                                viewModel.onEvent(ScanEvent.Scan(bytes, agent))
                            }
                            override fun onError(exc: ImageCaptureException) {
                                viewModel.onEvent(ScanEvent.ScanFailed(exc.message ?: "Capture failed"))
                            }
                        }
                    )
                }
            )
            else -> PermissionDeniedContent(
                onRequestPermission = { permissionLauncher.launch(Manifest.permission.CAMERA) }
            )
        }

        if (showPickerSheet && state.selectedAgent != null) {
            ModalBottomSheet(
                onDismissRequest = {
                    showPickerSheet = false
                    viewModel.onEvent(ScanEvent.SelectAgent(null))
                },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                ImageSourceSheet(
                    agent = state.selectedAgent!!,
                    onTakePhoto = {
                        scope.launch { sheetState.hide() }
                        openCamera()
                    },
                    onPickGallery = {
                        scope.launch { sheetState.hide() }
                        openGallery()
                    }
                )
            }
        }
    }
}


private val ScreenBg = Color(0xFFF8FBF4)
private val AccentGreen = Color(0xFF138A45)
private val SubtleBorder = Color(0xFFE3ECDC)

@Composable
private fun AgentGridContent(onAgentSelected: (AgentKey) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 32.dp, bottom = 28.dp)
    ) {
        Text(
            text = "Choose an Expert",
            color = TextPrimary,
            fontSize = 30.sp,
            fontWeight = FontWeight.Black,
            fontFamily = ClashDisplay
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Pick the specialist that fits your concern. They will analyse the photo you provide.",
            color = TextPrimary.copy(alpha = 0.7f),
            fontSize = 14.sp,
            lineHeight = 20.sp
        )

        Spacer(Modifier.height(22.dp))

        val agents = remember { AgentKey.values().toList() }
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 460.dp),
            userScrollEnabled = false,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(agents, key = { it.wireValue }) { agent ->
                AgentCard(agent = agent, onClick = { onAgentSelected(agent) })
            }
        }
    }
}

@Composable
private fun AgentCard(agent: AgentKey, onClick: () -> Unit) {
    val shape = RoundedCornerShape(22.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .shadow(elevation = 6.dp, shape = shape, ambientColor = agent.accent.copy(alpha = 0.18f))
            .clip(shape)
            .background(agent.cardBg)
            .border(BorderStroke(1.dp, agent.accent.copy(alpha = 0.25f)), shape)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(text = agent.emoji, fontSize = 28.sp)
        }
        Column {
            Text(
                text = agent.title,
                color = TextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 21.sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = agent.tagline,
                color = TextPrimary.copy(alpha = 0.7f),
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Analyse",
                    color = agent.accent,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(6.dp))
                Text(text = "›", color = agent.accent, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ImageSourceSheet(
    agent: AgentKey,
    onTakePhoto: () -> Unit,
    onPickGallery: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 4.dp, bottom = 28.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(agent.cardBg),
                contentAlignment = Alignment.Center
            ) {
                Text(text = agent.emoji, fontSize = 22.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = agent.title,
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Provide a photo to analyse",
                    color = TextPrimary.copy(alpha = 0.65f),
                    fontSize = 13.sp
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        SourceOptionRow(
            icon = Icons.Default.CameraAlt,
            title = "Take a Photo",
            subtitle = "Open camera and capture now",
            accent = agent.accent,
            onClick = onTakePhoto
        )
        Spacer(Modifier.height(12.dp))
        SourceOptionRow(
            icon = Icons.Default.PhotoLibrary,
            title = "Choose from Gallery",
            subtitle = "Pick an existing image",
            accent = agent.accent,
            onClick = onPickGallery
        )
    }
}

@Composable
private fun SourceOptionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    accent: Color,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(18.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Color.White)
            .border(BorderStroke(1.dp, SubtleBorder), shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(accent.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                color = TextPrimary.copy(alpha = 0.65f),
                fontSize = 13.sp
            )
        }
        Text(text = "›", color = accent, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun CameraContent(
    isScanning: Boolean,
    onClose: () -> Unit,
    onScan: (ImageCapture) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val previewView = remember { PreviewView(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }

    LaunchedEffect(lifecycleOwner) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val cameraProvider = cameraProviderFuture.await()
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            CameraSelector.DEFAULT_BACK_CAMERA,
            preview,
            imageCapture
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(12.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.4f))
                .clickable { onClose() },
            contentAlignment = Alignment.Center
        ) {
            Text(text = "✕", color = Color.White, fontSize = 18.sp)
        }

        if (isScanning) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.55f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = PrimaryColor, strokeWidth = 3.dp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Identifying plant…",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else {
            IconButton(
                onClick = { onScan(imageCapture) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 48.dp)
                    .size(72.dp)
                    .background(color = PrimaryColor, shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Scan plant",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
private fun PermissionDeniedContent(onRequestPermission: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("📷", fontSize = 52.sp)
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Camera access needed",
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Grant camera permission to scan and identify plants with AI.",
            color = TextPrimary.copy(alpha = 0.65f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(28.dp))
        OutlinedButton(onClick = onRequestPermission) {
            Text(
                text = "Grant Permission",
                color = PrimaryColor,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
