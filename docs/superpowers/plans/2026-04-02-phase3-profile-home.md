# Phase 3: Profile Screen + Home Dashboard

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build the Profile screen (user info + logout) and upgrade the Home tab from a placeholder into a live dashboard showing plant care reminders.

**Architecture:**
- `ProfileScreen` lives in the outer `mainNavGraph` (no bottom nav) — allows full back-stack clear on logout to navigate to auth
- `MainScreen` gains an optional `outerNavController: NavHostController = rememberNavController()` parameter so existing `MainScreenTest` keeps compiling unchanged
- `HomePage` receives the `outerNavController` to navigate to Profile
- `HomeViewModel` reads plants via `GetPlantsUseCase` and computes which ones need watering (`now - lastWatered >= wateringIntervalDays * 86400000L`)
- `LogoutUseCase` calls `AuthRepository.signOut()` (non-suspend) then `LocalCacheManager.clearAll()`

**Tech Stack:** Firebase Auth, Dagger Hilt, Jetpack Compose, Navigation, Material3

---

## File Map

| File | Action | Responsibility |
|------|--------|----------------|
| `presentation/auth/data/usecases/LogoutUseCase.kt` | Create | Calls signOut + clearAll |
| `presentation/profile/viewmodel/ProfileEvent.kt` | Create | Profile events |
| `presentation/profile/viewmodel/ProfileState.kt` | Create | Profile state |
| `presentation/profile/viewmodel/ProfileViewModel.kt` | Create | Logout logic |
| `presentation/profile/ui/ProfileScreen.kt` | Create | Profile UI |
| `navgraph/MainNavGraph.kt` | Modify | Add Profile route, pass navController to MainScreen |
| `presentation/main/ui/MainScreen.kt` | Modify | Accept outerNavController, pass to HomePage, add Profile route |
| `presentation/home/viewmodel/HomeEvent.kt` | Create | Home events |
| `presentation/home/viewmodel/HomeState.kt` | Create | Home state |
| `presentation/home/viewmodel/HomeViewModel.kt` | Create | Load plants + compute care reminders |
| `presentation/home/ui/HomePage.kt` | Modify | Live dashboard with care reminders + profile icon |

All paths relative to `app/src/main/java/ai/lufious/app/` unless noted.

---

## Task 1: LogoutUseCase + ProfileScreen

**Files:**
- Create: `app/src/main/java/ai/lufious/app/presentation/auth/data/usecases/LogoutUseCase.kt`
- Create: `app/src/main/java/ai/lufious/app/presentation/profile/viewmodel/ProfileEvent.kt`
- Create: `app/src/main/java/ai/lufious/app/presentation/profile/viewmodel/ProfileState.kt`
- Create: `app/src/main/java/ai/lufious/app/presentation/profile/viewmodel/ProfileViewModel.kt`
- Create: `app/src/main/java/ai/lufious/app/presentation/profile/ui/ProfileScreen.kt`

- [ ] **Step 1: Create LogoutUseCase.kt**

```kotlin
package ai.lufious.app.presentation.auth.data.usecases

import ai.lufious.app.core.local_cache.LocalCacheManager
import ai.lufious.app.presentation.auth.data.repository.AuthRepository
import jakarta.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepo: AuthRepository,
    private val localCache: LocalCacheManager
) {
    operator fun invoke() {
        authRepo.signOut()
        localCache.clearAll()
    }
}
```

- [ ] **Step 2: Create ProfileEvent.kt**

```kotlin
package ai.lufious.app.presentation.profile.viewmodel

sealed class ProfileEvent {
    object Logout : ProfileEvent()
}
```

- [ ] **Step 3: Create ProfileState.kt**

```kotlin
package ai.lufious.app.presentation.profile.viewmodel

data class ProfileState(
    val displayName: String = "",
    val email: String = "",
    val isLoading: Boolean = false
)
```

- [ ] **Step 4: Create ProfileViewModel.kt**

```kotlin
package ai.lufious.app.presentation.profile.viewmodel

import ai.lufious.app.core.local_cache.LocalCacheManager
import ai.lufious.app.core.utils.BaseViewModel
import ai.lufious.app.core.utils.DispatcherProvider
import ai.lufious.app.core.utils.UiEffect
import ai.lufious.app.presentation.auth.data.usecases.LogoutUseCase
import ai.lufious.app.core.utils.AUTH_GRAPH
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val logout: LogoutUseCase,
    private val localCache: LocalCacheManager,
    dispatchers: DispatcherProvider
) : BaseViewModel<ProfileEvent, ProfileState>(ProfileState(), dispatchers) {

    init {
        val user = localCache.getUser()
        setState {
            copy(
                displayName = user?.displayName ?: user?.email?.substringBefore("@") ?: "Gardener",
                email = user?.email ?: ""
            )
        }
    }

    fun onEvent(event: ProfileEvent) {
        viewModelScope.launch { handleEvent(event) }
    }

    override suspend fun handleEvent(event: ProfileEvent) {
        when (event) {
            ProfileEvent.Logout -> {
                setState { copy(isLoading = true) }
                logout()
                emitEffect(UiEffect.Navigate(AUTH_GRAPH))
            }
        }
    }
}
```

- [ ] **Step 5: Create ProfileScreen.kt**

```kotlin
package ai.lufious.app.presentation.profile.ui

import ai.lufious.app.core.theme.Background
import ai.lufious.app.core.theme.PrimaryColor
import ai.lufious.app.core.utils.AUTH_GRAPH
import ai.lufious.app.core.utils.BACK_BUTTON_HEIGHT_FRACTION
import ai.lufious.app.core.utils.UiEffect
import ai.lufious.app.core.utils.hR
import ai.lufious.app.core.utils.rememberResponsiveDimensions
import ai.lufious.app.core.utils.wR
import ai.lufious.app.presentation.profile.viewmodel.ProfileEvent
import ai.lufious.app.presentation.profile.viewmodel.ProfileViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val dimensions = rememberResponsiveDimensions()

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is UiEffect.Navigate -> {
                    navController.navigate(effect.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
                else -> {}
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimensions.heightFraction(BACK_BUTTON_HEIGHT_FRACTION).dp)
                    .padding(horizontal = dimensions.wR(8f).dp, vertical = dimensions.hR(8f).dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Profile",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .testTag("profile_screen"),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Avatar
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(color = PrimaryColor, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = state.displayName.firstOrNull()?.uppercaseChar()?.toString() ?: "G",
                    fontSize = 32.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = state.displayName,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = state.email,
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Sign out button
            Button(
                onClick = { viewModel.onEvent(ProfileEvent.Logout) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = !state.isLoading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE53935)
                )
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Sign Out",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
```

- [ ] **Step 6: Commit**

```bash
git add app/src/main/java/ai/lufious/app/presentation/auth/data/usecases/LogoutUseCase.kt \
        app/src/main/java/ai/lufious/app/presentation/profile/viewmodel/ProfileEvent.kt \
        app/src/main/java/ai/lufious/app/presentation/profile/viewmodel/ProfileState.kt \
        app/src/main/java/ai/lufious/app/presentation/profile/viewmodel/ProfileViewModel.kt \
        app/src/main/java/ai/lufious/app/presentation/profile/ui/ProfileScreen.kt
git commit -m "feat: add LogoutUseCase and ProfileScreen with sign-out"
```

---

## Task 2: Wire Profile into navigation + update MainScreen

**Files:**
- Modify: `app/src/main/java/ai/lufious/app/navgraph/MainNavGraph.kt`
- Modify: `app/src/main/java/ai/lufious/app/presentation/main/ui/MainScreen.kt`

- [ ] **Step 1: Update MainNavGraph.kt**

Full new content:

```kotlin
package ai.lufious.app.navgraph

import ai.lufious.app.core.utils.MAIN_GRAPH
import ai.lufious.app.core.utils.Screen
import ai.lufious.app.navgraph.utils.animatedComposable
import ai.lufious.app.presentation.main.ui.MainScreen
import ai.lufious.app.presentation.profile.ui.ProfileScreen
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation

fun NavGraphBuilder.mainNavGraph(
    navController: NavHostController
) {
    navigation(
        startDestination = Screen.Home.route,
        route = MAIN_GRAPH
    ) {
        animatedComposable(Screen.Home.route) {
            MainScreen(outerNavController = navController)
        }
        animatedComposable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }
    }
}
```

- [ ] **Step 2: Update MainScreen.kt**

Full new content:

```kotlin
package ai.lufious.app.presentation.main.ui

import ai.lufious.app.core.theme.Background
import ai.lufious.app.core.utils.Screen
import ai.lufious.app.presentation.garden.ui.AddPlantScreen
import ai.lufious.app.presentation.garden.ui.GardenPage
import ai.lufious.app.presentation.garden.ui.PlantDetailScreen
import ai.lufious.app.presentation.home.ui.HomePage
import ai.lufious.app.presentation.scan.ui.ScanPage
import ai.lufious.app.presentation.shop.ui.ShopPage
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun MainScreen(outerNavController: NavHostController = rememberNavController()) {
    val tabNavController = rememberNavController()

    Scaffold(
        containerColor = Background,
        bottomBar = { BottomNavigationBar(navController = tabNavController) }
    ) { paddingValues ->
        NavHost(
            navController = tabNavController,
            startDestination = Screen.HomeTab.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            composable(Screen.HomeTab.route) {
                HomePage(outerNavController = outerNavController)
            }
            composable(Screen.ScanTab.route) { ScanPage() }
            composable(Screen.GardenTab.route) {
                GardenPage(navController = tabNavController)
            }
            composable(Screen.ShopTab.route) { ShopPage() }
            composable(Screen.AddPlant.route) {
                AddPlantScreen(navController = tabNavController)
            }
            composable(
                route = Screen.PlantDetail.route,
                arguments = listOf(navArgument("plantId") { type = NavType.StringType })
            ) {
                PlantDetailScreen(navController = tabNavController)
            }
        }
    }
}
```

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/ai/lufious/app/navgraph/MainNavGraph.kt \
        app/src/main/java/ai/lufious/app/presentation/main/ui/MainScreen.kt
git commit -m "feat: wire ProfileScreen into outer nav graph, pass outerNavController to MainScreen"
```

---

## Task 3: HomeViewModel + HomePage dashboard

**Files:**
- Create: `app/src/main/java/ai/lufious/app/presentation/home/viewmodel/HomeEvent.kt`
- Create: `app/src/main/java/ai/lufious/app/presentation/home/viewmodel/HomeState.kt`
- Create: `app/src/main/java/ai/lufious/app/presentation/home/viewmodel/HomeViewModel.kt`
- Modify: `app/src/main/java/ai/lufious/app/presentation/home/ui/HomePage.kt`

- [ ] **Step 1: Create HomeEvent.kt**

```kotlin
package ai.lufious.app.presentation.home.viewmodel

sealed class HomeEvent {
    object LoadDashboard : HomeEvent()
}
```

- [ ] **Step 2: Create HomeState.kt**

```kotlin
package ai.lufious.app.presentation.home.viewmodel

import ai.lufious.app.presentation.garden.data.models.PlantModel

data class HomeState(
    val userName: String = "",
    val totalPlants: Int = 0,
    val plantsNeedingWater: List<PlantModel> = emptyList(),
    val isLoading: Boolean = false
)
```

- [ ] **Step 3: Create HomeViewModel.kt**

```kotlin
package ai.lufious.app.presentation.home.viewmodel

import ai.lufious.app.core.local_cache.LocalCacheManager
import ai.lufious.app.core.utils.BaseViewModel
import ai.lufious.app.core.utils.DispatcherProvider
import ai.lufious.app.core.utils.Result
import ai.lufious.app.presentation.garden.data.usecases.GetPlantsUseCase
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPlants: GetPlantsUseCase,
    private val localCache: LocalCacheManager,
    dispatchers: DispatcherProvider
) : BaseViewModel<HomeEvent, HomeState>(HomeState(), dispatchers) {

    init {
        onEvent(HomeEvent.LoadDashboard)
    }

    fun onEvent(event: HomeEvent) {
        viewModelScope.launch { handleEvent(event) }
    }

    override suspend fun handleEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.LoadDashboard -> {
                val user = localCache.getUser()
                val userName = user?.displayName
                    ?: user?.email?.substringBefore("@")
                    ?: "Gardener"
                setState { copy(userName = userName, isLoading = true) }
                ioLaunch {
                    when (val result = getPlants()) {
                        is Result.Success -> {
                            val plants = result.data ?: emptyList()
                            val now = System.currentTimeMillis()
                            val needsWater = plants.filter { plant ->
                                plant.wateringIntervalDays > 0 &&
                                    (now - plant.lastWatered) >= plant.wateringIntervalDays * 86_400_000L
                            }
                            setState {
                                copy(
                                    totalPlants = plants.size,
                                    plantsNeedingWater = needsWater,
                                    isLoading = false
                                )
                            }
                        }
                        is Result.Error ->
                            setState { copy(isLoading = false) }
                    }
                }
            }
        }
    }
}
```

- [ ] **Step 4: Update HomePage.kt**

Full new content:

```kotlin
package ai.lufious.app.presentation.home.ui

import ai.lufious.app.core.theme.Background
import ai.lufious.app.core.theme.HealthyGreen
import ai.lufious.app.core.theme.PrimaryColor
import ai.lufious.app.core.theme.WarningOrange
import ai.lufious.app.core.utils.Screen
import ai.lufious.app.presentation.home.viewmodel.HomeViewModel
import ai.lufious.app.presentation.garden.data.models.PlantModel
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

@Composable
fun HomePage(
    outerNavController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val greeting = when (java.time.LocalTime.now().hour) {
        in 5..11 -> "Good morning"
        in 12..17 -> "Good afternoon"
        else -> "Good evening"
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 32.dp)
            .testTag("home_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "$greeting 🌿",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = state.userName,
                        color = Color.White.copy(alpha = 0.65f),
                        fontSize = 14.sp
                    )
                }
                IconButton(
                    onClick = { outerNavController.navigate(Screen.Profile.route) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }

        item {
            // Summary card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.15f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatChip(
                        value = state.totalPlants.toString(),
                        label = "Plants",
                        color = HealthyGreen
                    )
                    StatChip(
                        value = state.plantsNeedingWater.size.toString(),
                        label = "Need water",
                        color = if (state.plantsNeedingWater.isEmpty()) HealthyGreen else WarningOrange
                    )
                }
            }
        }

        if (state.isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryColor)
                }
            }
        } else if (state.plantsNeedingWater.isNotEmpty()) {
            item {
                Text(
                    text = "💧 Needs water",
                    color = WarningOrange,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            items(state.plantsNeedingWater, key = { it.id }) { plant ->
                WaterReminderCard(plant = plant)
            }
        } else if (state.totalPlants > 0) {
            item {
                Text(
                    text = "✅ All plants are happy!",
                    color = HealthyGreen,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun StatChip(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = color,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 12.sp
        )
    }
}

@Composable
private fun WaterReminderCard(plant: PlantModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color.White.copy(alpha = 0.06f),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(color = WarningOrange.copy(alpha = 0.2f), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = plant.nickname.firstOrNull()?.uppercaseChar()?.toString() ?: "🌱",
                fontSize = 16.sp,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = plant.nickname,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Due for watering",
                color = WarningOrange,
                fontSize = 12.sp
            )
        }
    }
}
```

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/ai/lufious/app/presentation/home/viewmodel/HomeEvent.kt \
        app/src/main/java/ai/lufious/app/presentation/home/viewmodel/HomeState.kt \
        app/src/main/java/ai/lufious/app/presentation/home/viewmodel/HomeViewModel.kt \
        app/src/main/java/ai/lufious/app/presentation/home/ui/HomePage.kt
git commit -m "feat: add HomeViewModel and upgrade HomePage to live care dashboard"
```

---

## Verification

After signing in, confirm on device/emulator:
- [ ] Home tab shows greeting with user name and profile icon
- [ ] Summary card shows total plant count and water-needed count
- [ ] Plants due for watering appear as reminder cards
- [ ] Tapping profile icon navigates to Profile screen (no bottom nav)
- [ ] Profile screen shows user avatar initial, name, email
- [ ] "Sign Out" button logs out and navigates to the onboarding/login screen
