# Phase 1: Navigation Shell Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build the 4-tab main app shell (Home, Scan, Garden, Shop) with a bottom navigation bar, replacing the current empty stub.

**Architecture:** `MainNavGraph` navigates to `MainScreen`, which owns a `Scaffold` with a `BottomNavigationBar` and an inner `NavHost` that switches between 4 tab composables. The outer `NavController` (owned by `MainActivity`) handles auth↔main graph transitions; the inner `NavController` (owned by `MainScreen`) handles tab switching. Each tab composable is a placeholder for now.

**Tech Stack:** Jetpack Compose, Compose Navigation, `androidx.compose.material.BottomNavigation`, Material3 Scaffold, Material Icons Extended (already in `build.gradle.kts`)

---

## File Map

| File | Action | Responsibility |
|------|--------|----------------|
| `core/utils/Routes.kt` | Modify | Add 4 tab routes: HomeTab, ScanTab, GardenTab, ShopTab |
| `presentation/main/ui/BottomNavigationBar.kt` | Create | 4-item bottom nav bar, reads current route from inner NavController |
| `presentation/main/ui/MainScreen.kt` | Create | Scaffold shell — owns inner NavController + tab NavHost |
| `presentation/home/ui/HomePage.kt` | Modify | Static home placeholder (time-based greeting + coming soon) |
| `presentation/scan/ui/ScanPage.kt` | Create | Scan tab placeholder |
| `presentation/garden/ui/GardenPage.kt` | Create | Garden tab placeholder |
| `presentation/shop/ui/ShopPage.kt` | Create | Shop tab placeholder |
| `navgraph/MainNavGraph.kt` | Modify | Route `Screen.Home.route` → `MainScreen()` |

All paths relative to `app/src/main/java/ai/lufious/app/`.

---

## Task 1: Add tab routes to Routes.kt

**Files:**
- Modify: `app/src/main/java/ai/lufious/app/core/utils/Routes.kt`

- [ ] **Step 1: Add 4 tab objects inside the Screen sealed class**

Full new content of `Routes.kt`:
```kotlin
package ai.lufious.app.core.utils

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object GetStarted : Screen("get_started")
    object Login : Screen("auth/login")
    object EmailLogin : Screen("auth/login/email")
    object Signup : Screen("signup")
    object EmailSignup : Screen("signup/email")
    object Home : Screen("main/home")
    object Profile : Screen("main/profile")

    // Tab destinations — used within MainScreen's inner NavHost
    object HomeTab : Screen("tab/home")
    object ScanTab : Screen("tab/scan")
    object GardenTab : Screen("tab/garden")
    object ShopTab : Screen("tab/shop")
}

const val AUTH_GRAPH = "auth_graph"
const val MAIN_GRAPH = "main_graph"
```

- [ ] **Step 2: Commit**
```bash
git add app/src/main/java/ai/lufious/app/core/utils/Routes.kt
git commit -m "feat: add tab routes for bottom navigation"
```

---

## Task 2: Create stub tab screens (Scan, Garden, Shop)

**Files:**
- Create: `app/src/main/java/ai/lufious/app/presentation/scan/ui/ScanPage.kt`
- Create: `app/src/main/java/ai/lufious/app/presentation/garden/ui/GardenPage.kt`
- Create: `app/src/main/java/ai/lufious/app/presentation/shop/ui/ShopPage.kt`

- [ ] **Step 1: Create ScanPage.kt**

```kotlin
package ai.lufious.app.presentation.scan.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ScanPage(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("scan_screen"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("📷", fontSize = 48.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Scan",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "AI plant identification coming soon",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 14.sp
        )
    }
}
```

- [ ] **Step 2: Create GardenPage.kt**

```kotlin
package ai.lufious.app.presentation.garden.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GardenPage(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("garden_screen"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🌿", fontSize = 48.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Garden",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Your plant collection coming soon",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 14.sp
        )
    }
}
```

- [ ] **Step 3: Create ShopPage.kt**

```kotlin
package ai.lufious.app.presentation.shop.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ShopPage(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("shop_screen"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🛍️", fontSize = 48.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Shop",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Marketplace coming soon",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 14.sp
        )
    }
}
```

- [ ] **Step 4: Commit**
```bash
git add app/src/main/java/ai/lufious/app/presentation/scan/ui/ScanPage.kt \
        app/src/main/java/ai/lufious/app/presentation/garden/ui/GardenPage.kt \
        app/src/main/java/ai/lufious/app/presentation/shop/ui/ShopPage.kt
git commit -m "feat: add stub screens for Scan, Garden, Shop tabs"
```

---

## Task 3: Create BottomNavigationBar

**Files:**
- Create: `app/src/main/java/ai/lufious/app/presentation/main/ui/BottomNavigationBar.kt`

- [ ] **Step 1: Create BottomNavigationBar.kt**

```kotlin
package ai.lufious.app.presentation.main.ui

import ai.lufious.app.core.theme.Background
import ai.lufious.app.core.theme.PrimaryColor
import ai.lufious.app.core.utils.Screen
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem("Home", Icons.Default.Home, Screen.HomeTab.route),
        BottomNavItem("Scan", Icons.Default.PhotoCamera, Screen.ScanTab.route),
        BottomNavItem("Garden", Icons.Default.LocalFlorist, Screen.GardenTab.route),
        BottomNavItem("Shop", Icons.Default.ShoppingBag, Screen.ShopTab.route),
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    BottomNavigation(
        backgroundColor = Background,
        contentColor = Color.White
    ) {
        items.forEach { item ->
            BottomNavigationItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                label = { Text(text = item.label, fontSize = 10.sp) },
                selectedContentColor = PrimaryColor,
                unselectedContentColor = Color.White.copy(alpha = 0.5f)
            )
        }
    }
}
```

- [ ] **Step 2: Commit**
```bash
git add app/src/main/java/ai/lufious/app/presentation/main/ui/BottomNavigationBar.kt
git commit -m "feat: add BottomNavigationBar component"
```

---

## Task 4: Update HomePage with static placeholder content

**Files:**
- Modify: `app/src/main/java/ai/lufious/app/presentation/home/ui/HomePage.kt`

- [ ] **Step 1: Replace the empty stub**

```kotlin
package ai.lufious.app.presentation.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalTime

@Composable
fun HomePage(modifier: Modifier = Modifier) {
    val greeting = when (LocalTime.now().hour) {
        in 5..11 -> "Good morning"
        in 12..17 -> "Good afternoon"
        else -> "Good evening"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen")
            .padding(horizontal = 20.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "$greeting 🌿",
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Let's take care of your plants",
            color = Color.White.copy(alpha = 0.65f),
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "Your dashboard is on its way.",
            color = Color.White.copy(alpha = 0.45f),
            fontSize = 14.sp
        )
    }
}
```

- [ ] **Step 2: Commit**
```bash
git add app/src/main/java/ai/lufious/app/presentation/home/ui/HomePage.kt
git commit -m "feat: update HomePage with static greeting placeholder"
```

---

## Task 5: Create MainScreen shell

**Files:**
- Create: `app/src/main/java/ai/lufious/app/presentation/main/ui/MainScreen.kt`
- Create: `app/src/androidTest/java/ai/lufious/app/presentation/main/ui/MainScreenTest.kt`

- [ ] **Step 1: Write the failing test first**

```kotlin
package ai.lufious.app.presentation.main.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun mainScreen_homeTabIsSelectedByDefault() {
        composeTestRule.setContent { MainScreen() }
        composeTestRule.onNodeWithText("Let's take care of your plants").assertIsDisplayed()
    }

    @Test
    fun mainScreen_tappingScanTab_showsScanContent() {
        composeTestRule.setContent { MainScreen() }
        // "Scan" appears in both the nav label and ScanPage heading — click the first
        composeTestRule.onAllNodesWithText("Scan").onFirst().performClick()
        composeTestRule.onNodeWithTag("scan_screen").assertIsDisplayed()
    }

    @Test
    fun mainScreen_tappingGardenTab_showsGardenContent() {
        composeTestRule.setContent { MainScreen() }
        composeTestRule.onAllNodesWithText("Garden").onFirst().performClick()
        composeTestRule.onNodeWithTag("garden_screen").assertIsDisplayed()
    }

    @Test
    fun mainScreen_tappingShopTab_showsShopContent() {
        composeTestRule.setContent { MainScreen() }
        composeTestRule.onAllNodesWithText("Shop").onFirst().performClick()
        composeTestRule.onNodeWithTag("shop_screen").assertIsDisplayed()
    }
}
```

- [ ] **Step 2: Run the test — confirm it fails with "class not found"**
```bash
./gradlew :app:connectedDevDebugAndroidTest \
  --tests "ai.lufious.app.presentation.main.ui.MainScreenTest" 2>&1 | tail -20
```
Expected: compilation error — `MainScreen` does not exist yet.

- [ ] **Step 3: Create MainScreen.kt**

```kotlin
package ai.lufious.app.presentation.main.ui

import ai.lufious.app.core.theme.Background
import ai.lufious.app.core.utils.Screen
import ai.lufious.app.presentation.garden.ui.GardenPage
import ai.lufious.app.presentation.home.ui.HomePage
import ai.lufious.app.presentation.scan.ui.ScanPage
import ai.lufious.app.presentation.shop.ui.ShopPage
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScreen() {
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
            composable(Screen.HomeTab.route) { HomePage() }
            composable(Screen.ScanTab.route) { ScanPage() }
            composable(Screen.GardenTab.route) { GardenPage() }
            composable(Screen.ShopTab.route) { ShopPage() }
        }
    }
}
```

- [ ] **Step 4: Run the tests — confirm they pass**
```bash
./gradlew :app:connectedDevDebugAndroidTest \
  --tests "ai.lufious.app.presentation.main.ui.MainScreenTest" 2>&1 | tail -20
```
Expected: 4 tests PASS.

- [ ] **Step 5: Commit**
```bash
git add app/src/main/java/ai/lufious/app/presentation/main/ui/MainScreen.kt \
        app/src/androidTest/java/ai/lufious/app/presentation/main/ui/MainScreenTest.kt
git commit -m "feat: add MainScreen shell with bottom tab navigation"
```

---

## Task 6: Wire MainNavGraph to MainScreen

**Files:**
- Modify: `app/src/main/java/ai/lufious/app/navgraph/MainNavGraph.kt`

- [ ] **Step 1: Replace `HomePage()` with `MainScreen()`**

Full new content of `MainNavGraph.kt`:
```kotlin
package ai.lufious.app.navgraph

import ai.lufious.app.core.utils.MAIN_GRAPH
import ai.lufious.app.core.utils.Screen
import ai.lufious.app.navgraph.utils.animatedComposable
import ai.lufious.app.presentation.main.ui.MainScreen
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
            MainScreen()
        }
    }
}
```

- [ ] **Step 2: Commit**
```bash
git add app/src/main/java/ai/lufious/app/navgraph/MainNavGraph.kt
git commit -m "feat: wire MainNavGraph to MainScreen shell"
```

---

## Verification

After signing in, confirm on device/emulator:

- [ ] Home tab shown by default — time-based greeting ("Good morning/afternoon/evening 🌿") visible
- [ ] "Let's take care of your plants" subtitle visible
- [ ] Tap **Scan** — shows "📷 Scan / AI plant identification coming soon"
- [ ] Tap **Garden** — shows "🌿 Garden / Your plant collection coming soon"
- [ ] Tap **Shop** — shows "🛍️ Shop / Marketplace coming soon"
- [ ] Tap **Home** — returns to greeting
- [ ] Selected tab icon/label is green (`#35A924`), unselected tabs are dimmed
- [ ] Bottom nav background matches the app's dark green background
- [ ] App bar is hidden (system bars hidden from MainActivity — content fills edge-to-edge)
