# Phase 2: Garden Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build the Garden tab — a plant collection grid, add-plant form (manual), plant detail view, and care logging — backed by Firestore subcollections.

**Architecture:** Each plant is stored at `users/{uid}/plants/{plantId}` in Firestore; care logs at `users/{uid}/plants/{plantId}/logs/{logId}`. Three screens live in the inner NavHost of `MainScreen`: `GardenPage` (grid + FAB), `AddPlantScreen` (form), `PlantDetailScreen` (plant info + care log). Each screen uses the existing `BaseViewModel` pattern: DataSource → PlantRepository → UseCases → ViewModel → Composable. Hilt test infrastructure is added so ViewModel-connected screens can be tested.

**Tech Stack:** Firebase Firestore, Dagger Hilt, Jetpack Compose, Compose Navigation, Material3 (Scaffold, FAB, AlertDialog, ExposedDropdownMenuBox, Card), Material 1 (Text, CircularProgressIndicator, CommonTextField, ShadowButton)

---

## File Map

| File | Action | Responsibility |
|------|--------|----------------|
| `core/utils/Routes.kt` | Modify | Add `Screen.AddPlant` and `Screen.PlantDetail` routes |
| `core/firebase/utils/FirestoreModels.kt` | Modify | Add `PlantFields` and `LogFields` constants |
| `presentation/garden/data/models/PlantModel.kt` | Create | Plant domain model |
| `presentation/garden/data/models/CareLogModel.kt` | Create | Care log domain model |
| `presentation/garden/data/datasource/PlantDataSource.kt` | Create | Firestore CRUD for plants and care logs |
| `presentation/garden/data/repository/PlantRepository.kt` | Create | Repository interface |
| `presentation/garden/data/repository/PlantRepositoryImpl.kt` | Create | Repository implementation |
| `presentation/garden/data/usecases/PlantUseCases.kt` | Create | 5 use cases in one file |
| `di/GardenModule.kt` | Create | Hilt binding for PlantRepository |
| `presentation/garden/viewmodel/GardenEvent.kt` | Create | Garden screen events |
| `presentation/garden/viewmodel/GardenState.kt` | Create | Garden screen state |
| `presentation/garden/viewmodel/GardenViewModel.kt` | Create | Loads plant list |
| `presentation/garden/ui/PlantCard.kt` | Create | Plant card composable for grid |
| `presentation/garden/ui/GardenPage.kt` | Modify | Plant grid + FAB, replace stub |
| `presentation/garden/viewmodel/AddPlantEvent.kt` | Create | Add plant form events |
| `presentation/garden/viewmodel/AddPlantState.kt` | Create | Add plant form state |
| `presentation/garden/viewmodel/AddPlantViewModel.kt` | Create | Add plant form logic |
| `presentation/garden/ui/AddPlantScreen.kt` | Create | Add plant form screen |
| `presentation/garden/viewmodel/PlantDetailEvent.kt` | Create | Plant detail screen events |
| `presentation/garden/viewmodel/PlantDetailState.kt` | Create | Plant detail screen state |
| `presentation/garden/viewmodel/PlantDetailViewModel.kt` | Create | Plant detail + care log logic |
| `presentation/garden/ui/PlantDetailScreen.kt` | Create | Plant info + care log screen |
| `presentation/main/ui/MainScreen.kt` | Modify | Add AddPlant + PlantDetail to inner NavHost; pass navController to GardenPage |
| `build.gradle.kts` | Modify | Add `hilt-android-testing` + `kaptAndroidTest` |
| `src/debug/java/ai/lufious/app/HiltTestActivity.kt` | Create | `@AndroidEntryPoint` activity for instrumented tests |
| `presentation/main/ui/MainScreenTest.kt` | Modify | Upgrade to `@HiltAndroidTest` with fake garden module |

All paths relative to `app/src/main/java/ai/lufious/app/` unless noted.

---

## Task 1: Routes + Firestore field constants

**Files:**
- Modify: `app/src/main/java/ai/lufious/app/core/utils/Routes.kt`
- Modify: `app/src/main/java/ai/lufious/app/core/firebase/utils/FirestoreModels.kt`

- [ ] **Step 1: Add garden sub-screen routes to Routes.kt**

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

    // Garden sub-screens — still within MainScreen's inner NavHost
    object AddPlant : Screen("garden/add_plant")
    object PlantDetail : Screen("garden/plant/{plantId}") {
        fun createRoute(plantId: String) = "garden/plant/$plantId"
    }
}

const val AUTH_GRAPH = "auth_graph"
const val MAIN_GRAPH = "main_graph"
```

- [ ] **Step 2: Add PlantFields and LogFields to FirestoreModels.kt**

Full new content of `FirestoreModels.kt`:

```kotlin
package ai.lufious.app.core.firebase.utils

sealed class FirestoreModels(val collection: String) {
    object User : FirestoreModels("users") {
        const val NAME = "name"
        const val EMAIL = "email"
        const val AGE = "age"
    }
}

object PlantFields {
    const val COLLECTION = "plants"
    const val NICKNAME = "nickname"
    const val SPECIES = "species"
    const val PHOTO_URL = "photoUrl"
    const val LOCATION_TAG = "locationTag"
    const val WATERING_INTERVAL_DAYS = "wateringIntervalDays"
    const val FERTILIZING_INTERVAL_DAYS = "fertilizingIntervalDays"
    const val LAST_WATERED = "lastWatered"
    const val LAST_FERTILIZED = "lastFertilized"
    const val ADDED_AT = "addedAt"
    const val HEALTH_STATUS = "healthStatus"
}

object LogFields {
    const val COLLECTION = "logs"
    const val TYPE = "type"
    const val NOTE = "note"
    const val TIMESTAMP = "timestamp"
}
```

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/ai/lufious/app/core/utils/Routes.kt \
        app/src/main/java/ai/lufious/app/core/firebase/utils/FirestoreModels.kt
git commit -m "feat: add garden routes and Firestore field constants"
```

---

## Task 2: Data models

**Files:**
- Create: `app/src/main/java/ai/lufious/app/presentation/garden/data/models/PlantModel.kt`
- Create: `app/src/main/java/ai/lufious/app/presentation/garden/data/models/CareLogModel.kt`

- [ ] **Step 1: Create PlantModel.kt**

```kotlin
package ai.lufious.app.presentation.garden.data.models

data class PlantModel(
    val id: String = "",
    val nickname: String = "",
    val species: String = "",
    val photoUrl: String = "",
    val locationTag: String = "Living Room",
    val wateringIntervalDays: Int = 7,
    val fertilizingIntervalDays: Int = 30,
    val lastWatered: Long = 0L,
    val lastFertilized: Long = 0L,
    val addedAt: Long = 0L,
    val healthStatus: String = "healthy"
)
```

- [ ] **Step 2: Create CareLogModel.kt**

```kotlin
package ai.lufious.app.presentation.garden.data.models

data class CareLogModel(
    val id: String = "",
    val type: String = "",
    val note: String = "",
    val timestamp: Long = 0L
)
```

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/ai/lufious/app/presentation/garden/data/models/PlantModel.kt \
        app/src/main/java/ai/lufious/app/presentation/garden/data/models/CareLogModel.kt
git commit -m "feat: add PlantModel and CareLogModel data classes"
```

---

## Task 3: Data layer — DataSource, Repository, Hilt module

**Files:**
- Create: `app/src/main/java/ai/lufious/app/presentation/garden/data/datasource/PlantDataSource.kt`
- Create: `app/src/main/java/ai/lufious/app/presentation/garden/data/repository/PlantRepository.kt`
- Create: `app/src/main/java/ai/lufious/app/presentation/garden/data/repository/PlantRepositoryImpl.kt`
- Create: `app/src/main/java/ai/lufious/app/di/GardenModule.kt`

- [ ] **Step 1: Create PlantDataSource.kt**

```kotlin
package ai.lufious.app.presentation.garden.data.datasource

import ai.lufious.app.core.firebase.utils.LogFields
import ai.lufious.app.core.firebase.utils.PlantFields
import ai.lufious.app.core.local_cache.LocalCacheManager
import ai.lufious.app.presentation.garden.data.models.CareLogModel
import ai.lufious.app.presentation.garden.data.models.PlantModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import jakarta.inject.Inject
import kotlinx.coroutines.tasks.await

class PlantDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val localCache: LocalCacheManager
) {
    private val uid get() = localCache.getUser()?.uid ?: error("User not logged in")

    private fun plantsRef() =
        firestore.collection("users").document(uid).collection(PlantFields.COLLECTION)

    private fun logsRef(plantId: String) =
        plantsRef().document(plantId).collection(LogFields.COLLECTION)

    suspend fun addPlant(plant: PlantModel): PlantModel {
        val doc = plantsRef().document()
        val withId = plant.copy(id = doc.id, addedAt = System.currentTimeMillis())
        doc.set(withId.toMap()).await()
        return withId
    }

    suspend fun getPlants(): List<PlantModel> =
        plantsRef()
            .orderBy(PlantFields.ADDED_AT, Query.Direction.DESCENDING)
            .get().await()
            .documents
            .mapNotNull { it.toPlantModel() }

    suspend fun getPlantById(plantId: String): PlantModel =
        plantsRef().document(plantId).get().await().toPlantModel()
            ?: error("Plant $plantId not found")

    suspend fun addCareLog(plantId: String, log: CareLogModel): CareLogModel {
        val doc = logsRef(plantId).document()
        val withId = log.copy(id = doc.id, timestamp = System.currentTimeMillis())
        doc.set(withId.toMap()).await()
        return withId
    }

    suspend fun getCareLogs(plantId: String): List<CareLogModel> =
        logsRef(plantId)
            .orderBy(LogFields.TIMESTAMP, Query.Direction.DESCENDING)
            .get().await()
            .documents
            .mapNotNull { it.toCareLogModel() }

    private fun PlantModel.toMap(): Map<String, Any> = mapOf(
        PlantFields.NICKNAME to nickname,
        PlantFields.SPECIES to species,
        PlantFields.PHOTO_URL to photoUrl,
        PlantFields.LOCATION_TAG to locationTag,
        PlantFields.WATERING_INTERVAL_DAYS to wateringIntervalDays,
        PlantFields.FERTILIZING_INTERVAL_DAYS to fertilizingIntervalDays,
        PlantFields.LAST_WATERED to lastWatered,
        PlantFields.LAST_FERTILIZED to lastFertilized,
        PlantFields.ADDED_AT to addedAt,
        PlantFields.HEALTH_STATUS to healthStatus
    )

    private fun CareLogModel.toMap(): Map<String, Any> = mapOf(
        LogFields.TYPE to type,
        LogFields.NOTE to note,
        LogFields.TIMESTAMP to timestamp
    )

    private fun DocumentSnapshot.toPlantModel(): PlantModel? = try {
        PlantModel(
            id = id,
            nickname = getString(PlantFields.NICKNAME) ?: "",
            species = getString(PlantFields.SPECIES) ?: "",
            photoUrl = getString(PlantFields.PHOTO_URL) ?: "",
            locationTag = getString(PlantFields.LOCATION_TAG) ?: "Living Room",
            wateringIntervalDays = getLong(PlantFields.WATERING_INTERVAL_DAYS)?.toInt() ?: 7,
            fertilizingIntervalDays = getLong(PlantFields.FERTILIZING_INTERVAL_DAYS)?.toInt() ?: 30,
            lastWatered = getLong(PlantFields.LAST_WATERED) ?: 0L,
            lastFertilized = getLong(PlantFields.LAST_FERTILIZED) ?: 0L,
            addedAt = getLong(PlantFields.ADDED_AT) ?: 0L,
            healthStatus = getString(PlantFields.HEALTH_STATUS) ?: "healthy"
        )
    } catch (e: Exception) { null }

    private fun DocumentSnapshot.toCareLogModel(): CareLogModel? = try {
        CareLogModel(
            id = id,
            type = getString(LogFields.TYPE) ?: "",
            note = getString(LogFields.NOTE) ?: "",
            timestamp = getLong(LogFields.TIMESTAMP) ?: 0L
        )
    } catch (e: Exception) { null }
}
```

- [ ] **Step 2: Create PlantRepository.kt**

```kotlin
package ai.lufious.app.presentation.garden.data.repository

import ai.lufious.app.core.utils.Result
import ai.lufious.app.presentation.garden.data.models.CareLogModel
import ai.lufious.app.presentation.garden.data.models.PlantModel

interface PlantRepository {
    suspend fun addPlant(
        nickname: String,
        species: String,
        locationTag: String,
        wateringIntervalDays: Int
    ): Result<PlantModel>

    suspend fun getPlants(): Result<List<PlantModel>>

    suspend fun getPlantById(plantId: String): Result<PlantModel>

    suspend fun logCareAction(
        plantId: String,
        type: String,
        note: String
    ): Result<CareLogModel>

    suspend fun getCareLogs(plantId: String): Result<List<CareLogModel>>
}
```

- [ ] **Step 3: Create PlantRepositoryImpl.kt**

```kotlin
package ai.lufious.app.presentation.garden.data.repository

import ai.lufious.app.core.utils.Result
import ai.lufious.app.presentation.garden.data.datasource.PlantDataSource
import ai.lufious.app.presentation.garden.data.models.CareLogModel
import ai.lufious.app.presentation.garden.data.models.PlantModel
import jakarta.inject.Inject

class PlantRepositoryImpl @Inject constructor(
    private val ds: PlantDataSource
) : PlantRepository {

    override suspend fun addPlant(
        nickname: String,
        species: String,
        locationTag: String,
        wateringIntervalDays: Int
    ): Result<PlantModel> = wrap {
        ds.addPlant(
            PlantModel(
                nickname = nickname,
                species = species,
                locationTag = locationTag,
                wateringIntervalDays = wateringIntervalDays
            )
        )
    }

    override suspend fun getPlants(): Result<List<PlantModel>> = wrap { ds.getPlants() }

    override suspend fun getPlantById(plantId: String): Result<PlantModel> =
        wrap { ds.getPlantById(plantId) }

    override suspend fun logCareAction(
        plantId: String,
        type: String,
        note: String
    ): Result<CareLogModel> = wrap {
        ds.addCareLog(plantId, CareLogModel(type = type, note = note))
    }

    override suspend fun getCareLogs(plantId: String): Result<List<CareLogModel>> =
        wrap { ds.getCareLogs(plantId) }

    private suspend fun <T> wrap(block: suspend () -> T): Result<T> =
        try {
            Result.Success(block())
        } catch (e: Exception) {
            Result.Error(e.message.toString())
        }
}
```

- [ ] **Step 4: Create GardenModule.kt**

```kotlin
package ai.lufious.app.di

import ai.lufious.app.presentation.garden.data.repository.PlantRepository
import ai.lufious.app.presentation.garden.data.repository.PlantRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GardenModule {

    @Provides
    @Singleton
    fun providePlantRepository(impl: PlantRepositoryImpl): PlantRepository = impl
}
```

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/ai/lufious/app/presentation/garden/data/datasource/PlantDataSource.kt \
        app/src/main/java/ai/lufious/app/presentation/garden/data/repository/PlantRepository.kt \
        app/src/main/java/ai/lufious/app/presentation/garden/data/repository/PlantRepositoryImpl.kt \
        app/src/main/java/ai/lufious/app/di/GardenModule.kt
git commit -m "feat: add plant data layer (DataSource, Repository, Hilt module)"
```

---

## Task 4: Use cases

**Files:**
- Create: `app/src/main/java/ai/lufious/app/presentation/garden/data/usecases/PlantUseCases.kt`

- [ ] **Step 1: Create PlantUseCases.kt** (all 5 use cases in one file, following the auth pattern)

```kotlin
package ai.lufious.app.presentation.garden.data.usecases

import ai.lufious.app.core.utils.Result
import ai.lufious.app.presentation.garden.data.models.CareLogModel
import ai.lufious.app.presentation.garden.data.models.PlantModel
import ai.lufious.app.presentation.garden.data.repository.PlantRepository
import jakarta.inject.Inject

class AddPlantUseCase @Inject constructor(private val repo: PlantRepository) {
    suspend operator fun invoke(
        nickname: String,
        species: String,
        locationTag: String,
        wateringIntervalDays: Int
    ): Result<PlantModel> = repo.addPlant(nickname, species, locationTag, wateringIntervalDays)
}

class GetPlantsUseCase @Inject constructor(private val repo: PlantRepository) {
    suspend operator fun invoke(): Result<List<PlantModel>> = repo.getPlants()
}

class GetPlantByIdUseCase @Inject constructor(private val repo: PlantRepository) {
    suspend operator fun invoke(plantId: String): Result<PlantModel> =
        repo.getPlantById(plantId)
}

class LogCareActionUseCase @Inject constructor(private val repo: PlantRepository) {
    suspend operator fun invoke(
        plantId: String,
        type: String,
        note: String
    ): Result<CareLogModel> = repo.logCareAction(plantId, type, note)
}

class GetCareLogsUseCase @Inject constructor(private val repo: PlantRepository) {
    suspend operator fun invoke(plantId: String): Result<List<CareLogModel>> =
        repo.getCareLogs(plantId)
}
```

- [ ] **Step 2: Commit**

```bash
git add app/src/main/java/ai/lufious/app/presentation/garden/data/usecases/PlantUseCases.kt
git commit -m "feat: add plant use cases (AddPlant, GetPlants, GetPlantById, LogCare, GetCareLogs)"
```

---

## Task 5: Hilt test infrastructure + MainScreenTest update

`GardenPage` uses `hiltViewModel()`. Without Hilt test support, `MainScreenTest` crashes when it navigates to the Garden tab. This task adds the required infrastructure.

**Files:**
- Modify: `app/build.gradle.kts`
- Create: `app/src/debug/java/ai/lufious/app/HiltTestActivity.kt`
- Modify: `app/src/androidTest/java/ai/lufious/app/presentation/main/ui/MainScreenTest.kt`

- [ ] **Step 1: Add Hilt test dependencies to build.gradle.kts**

In `build.gradle.kts`, make two edits:

**Edit 1** — change `testInstrumentationRunner` inside `defaultConfig { }`:
```kotlin
testInstrumentationRunner = "dagger.hilt.android.testing.HiltTestRunner"
```

**Edit 2** — add these two lines after the `kapt(libs.hilt.compiler)` line in `dependencies { }`:
```kotlin
androidTestImplementation("com.google.dagger:hilt-android-testing:2.56.1")
kaptAndroidTest("com.google.dagger:hilt-android-compiler:2.56.1")
```

- [ ] **Step 2: Create the debug-only HiltTestActivity**

Create directory `app/src/debug/java/ai/lufious/app/` if it does not exist.

Create `app/src/debug/java/ai/lufious/app/HiltTestActivity.kt`:

```kotlin
package ai.lufious.app

import androidx.activity.ComponentActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HiltTestActivity : ComponentActivity()
```

- [ ] **Step 3: Update MainScreenTest.kt**

The test now uses `@HiltAndroidTest` and replaces the real `GardenModule` with a fake that returns an empty plant list immediately (no Firebase calls). This keeps the test fast and deterministic.

Full new content of `app/src/androidTest/java/ai/lufious/app/presentation/main/ui/MainScreenTest.kt`:

```kotlin
package ai.lufious.app.presentation.main.ui

import ai.lufious.app.HiltTestActivity
import ai.lufious.app.core.utils.Result
import ai.lufious.app.di.GardenModule
import ai.lufious.app.presentation.garden.data.models.CareLogModel
import ai.lufious.app.presentation.garden.data.models.PlantModel
import ai.lufious.app.presentation.garden.data.repository.PlantRepository
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Singleton

@HiltAndroidTest
@UninstallModules(GardenModule::class)
@RunWith(AndroidJUnit4::class)
class MainScreenTest {

    @Module
    @InstallIn(SingletonComponent::class)
    object FakeGardenModule {
        @Provides
        @Singleton
        fun providePlantRepository(): PlantRepository = object : PlantRepository {
            override suspend fun addPlant(
                nickname: String, species: String,
                locationTag: String, wateringIntervalDays: Int
            ) = Result.Success(PlantModel())
            override suspend fun getPlants() = Result.Success(emptyList<PlantModel>())
            override suspend fun getPlantById(plantId: String) = Result.Success(PlantModel())
            override suspend fun logCareAction(plantId: String, type: String, note: String) =
                Result.Success(CareLogModel())
            override suspend fun getCareLogs(plantId: String) =
                Result.Success(emptyList<CareLogModel>())
        }
    }

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun mainScreen_homeTabIsSelectedByDefault() {
        composeTestRule.setContent { MainScreen() }
        composeTestRule.onNodeWithTag("home_screen").assertIsDisplayed()
    }

    @Test
    fun mainScreen_tappingScanTab_showsScanContent() {
        composeTestRule.setContent { MainScreen() }
        composeTestRule.onAllNodesWithText("Scan").onFirst().performClick()
        composeTestRule.onNodeWithTag("scan_screen").assertIsDisplayed()
    }

    @Test
    fun mainScreen_tappingGardenTab_showsGardenContent() {
        composeTestRule.setContent { MainScreen() }
        composeTestRule.onAllNodesWithText("Garden").onFirst().performClick()
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("garden_screen").fetchSemanticsNodes().isNotEmpty()
        }
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

- [ ] **Step 4: Commit**

```bash
git add app/build.gradle.kts \
        app/src/debug/java/ai/lufious/app/HiltTestActivity.kt \
        app/src/androidTest/java/ai/lufious/app/presentation/main/ui/MainScreenTest.kt
git commit -m "feat: add Hilt test infrastructure and update MainScreenTest with fake garden module"
```

---

## Task 6: GardenViewModel + PlantCard + updated GardenPage

**Files:**
- Create: `app/src/main/java/ai/lufious/app/presentation/garden/viewmodel/GardenEvent.kt`
- Create: `app/src/main/java/ai/lufious/app/presentation/garden/viewmodel/GardenState.kt`
- Create: `app/src/main/java/ai/lufious/app/presentation/garden/viewmodel/GardenViewModel.kt`
- Create: `app/src/main/java/ai/lufious/app/presentation/garden/ui/PlantCard.kt`
- Modify: `app/src/main/java/ai/lufious/app/presentation/garden/ui/GardenPage.kt`

- [ ] **Step 1: Create GardenEvent.kt**

```kotlin
package ai.lufious.app.presentation.garden.viewmodel

sealed class GardenEvent {
    object LoadPlants : GardenEvent()
}
```

- [ ] **Step 2: Create GardenState.kt**

```kotlin
package ai.lufious.app.presentation.garden.viewmodel

import ai.lufious.app.presentation.garden.data.models.PlantModel

data class GardenState(
    val plants: List<PlantModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
```

- [ ] **Step 3: Create GardenViewModel.kt**

```kotlin
package ai.lufious.app.presentation.garden.viewmodel

import ai.lufious.app.core.utils.BaseViewModel
import ai.lufious.app.core.utils.DispatcherProvider
import ai.lufious.app.core.utils.Result
import ai.lufious.app.presentation.garden.data.usecases.GetPlantsUseCase
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GardenViewModel @Inject constructor(
    private val getPlants: GetPlantsUseCase,
    dispatchers: DispatcherProvider
) : BaseViewModel<GardenEvent, GardenState>(GardenState(), dispatchers) {

    init {
        onEvent(GardenEvent.LoadPlants)
    }

    fun onEvent(event: GardenEvent) {
        viewModelScope.launch { handleEvent(event) }
    }

    override suspend fun handleEvent(event: GardenEvent) {
        when (event) {
            GardenEvent.LoadPlants -> {
                setState { copy(isLoading = true, error = null) }
                ioLaunch {
                    when (val result = getPlants()) {
                        is Result.Success ->
                            setState { copy(plants = result.data ?: emptyList(), isLoading = false) }
                        is Result.Error ->
                            setState { copy(isLoading = false, error = result.message) }
                    }
                }
            }
        }
    }
}
```

- [ ] **Step 4: Create PlantCard.kt**

```kotlin
package ai.lufious.app.presentation.garden.ui

import ai.lufious.app.core.theme.CriticalRed
import ai.lufious.app.core.theme.HealthyGreen
import ai.lufious.app.core.theme.WarningOrange
import ai.lufious.app.presentation.garden.data.models.PlantModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PlantCard(
    plant: PlantModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val healthColor = when (plant.healthStatus) {
        "warning" -> WarningOrange
        "critical" -> CriticalRed
        else -> HealthyGreen
    }
    val initial = plant.nickname.firstOrNull()?.uppercaseChar()?.toString() ?: "🌱"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colors.onBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .background(
                        color = HealthyGreen.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initial,
                    fontSize = 28.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(color = healthColor, shape = CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = plant.nickname,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = plant.species,
                color = Color.White.copy(alpha = 0.55f),
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
```

- [ ] **Step 5: Update GardenPage.kt**

```kotlin
package ai.lufious.app.presentation.garden.ui

import ai.lufious.app.core.theme.Background
import ai.lufious.app.core.theme.PrimaryColor
import ai.lufious.app.core.utils.Screen
import ai.lufious.app.presentation.garden.viewmodel.GardenViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
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
import androidx.navigation.NavController

@Composable
fun GardenPage(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: GardenViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddPlant.route) },
                containerColor = PrimaryColor
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add plant",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        when {
            state.isLoading -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryColor)
            }

            state.plants.isEmpty() -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .testTag("garden_screen"),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("🌱", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No plants yet",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tap + to add your first plant",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 14.sp
                    )
                }
            }

            else -> LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 12.dp, vertical = 12.dp)
                    .testTag("garden_screen"),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.plants, key = { it.id }) { plant ->
                    PlantCard(
                        plant = plant,
                        onClick = {
                            navController.navigate(Screen.PlantDetail.createRoute(plant.id))
                        }
                    )
                }
            }
        }
    }
}
```

- [ ] **Step 6: Commit**

```bash
git add app/src/main/java/ai/lufious/app/presentation/garden/viewmodel/GardenEvent.kt \
        app/src/main/java/ai/lufious/app/presentation/garden/viewmodel/GardenState.kt \
        app/src/main/java/ai/lufious/app/presentation/garden/viewmodel/GardenViewModel.kt \
        app/src/main/java/ai/lufious/app/presentation/garden/ui/PlantCard.kt \
        app/src/main/java/ai/lufious/app/presentation/garden/ui/GardenPage.kt
git commit -m "feat: add GardenViewModel, PlantCard, and update GardenPage with plant grid"
```

---

## Task 7: AddPlantViewModel + AddPlantScreen

**Files:**
- Create: `app/src/main/java/ai/lufious/app/presentation/garden/viewmodel/AddPlantEvent.kt`
- Create: `app/src/main/java/ai/lufious/app/presentation/garden/viewmodel/AddPlantState.kt`
- Create: `app/src/main/java/ai/lufious/app/presentation/garden/viewmodel/AddPlantViewModel.kt`
- Create: `app/src/main/java/ai/lufious/app/presentation/garden/ui/AddPlantScreen.kt`
- Create: `app/src/androidTest/java/ai/lufious/app/presentation/garden/ui/AddPlantScreenTest.kt`

- [ ] **Step 1: Write the failing test first**

Create `app/src/androidTest/java/ai/lufious/app/presentation/garden/ui/AddPlantScreenTest.kt`:

```kotlin
package ai.lufious.app.presentation.garden.ui

import ai.lufious.app.HiltTestActivity
import ai.lufious.app.core.utils.Result
import ai.lufious.app.di.GardenModule
import ai.lufious.app.presentation.garden.data.models.CareLogModel
import ai.lufious.app.presentation.garden.data.models.PlantModel
import ai.lufious.app.presentation.garden.data.repository.PlantRepository
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Singleton

@HiltAndroidTest
@UninstallModules(GardenModule::class)
@RunWith(AndroidJUnit4::class)
class AddPlantScreenTest {

    @Module
    @InstallIn(SingletonComponent::class)
    object FakeGardenModule {
        @Provides
        @Singleton
        fun providePlantRepository(): PlantRepository = object : PlantRepository {
            override suspend fun addPlant(
                nickname: String, species: String,
                locationTag: String, wateringIntervalDays: Int
            ) = Result.Success(PlantModel(id = "1", nickname = nickname, species = species))
            override suspend fun getPlants() = Result.Success(emptyList<PlantModel>())
            override suspend fun getPlantById(plantId: String) = Result.Success(PlantModel())
            override suspend fun logCareAction(plantId: String, type: String, note: String) =
                Result.Success(CareLogModel())
            override suspend fun getCareLogs(plantId: String) =
                Result.Success(emptyList<CareLogModel>())
        }
    }

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun addPlantScreen_rendersFormFields() {
        composeTestRule.setContent {
            AddPlantScreen(navController = rememberNavController())
        }
        composeTestRule.onNodeWithTag("add_plant_screen").assertIsDisplayed()
        composeTestRule.onNodeWithText("Plant nickname").assertIsDisplayed()
        composeTestRule.onNodeWithText("Species name").assertIsDisplayed()
    }

    @Test
    fun addPlantScreen_typingInFields_doesNotCrash() {
        composeTestRule.setContent {
            AddPlantScreen(navController = rememberNavController())
        }
        composeTestRule.onNodeWithTag("nickname_field").performTextInput("My Monstera")
        composeTestRule.onNodeWithTag("species_field").performTextInput("Monstera Deliciosa")
        composeTestRule.onNodeWithTag("add_plant_screen").assertIsDisplayed()
    }
}
```

- [ ] **Step 2: Run test to confirm it fails**

```bash
./gradlew :app:connectedDevDebugAndroidTest \
  --tests "ai.lufious.app.presentation.garden.ui.AddPlantScreenTest" 2>&1 | tail -20
```

Expected: compilation error — `AddPlantScreen` does not exist yet.

- [ ] **Step 3: Create AddPlantEvent.kt**

```kotlin
package ai.lufious.app.presentation.garden.viewmodel

sealed class AddPlantEvent {
    data class NicknameChanged(val value: String) : AddPlantEvent()
    data class SpeciesChanged(val value: String) : AddPlantEvent()
    data class LocationChanged(val value: String) : AddPlantEvent()
    data class WateringIntervalChanged(val value: String) : AddPlantEvent()
    object Submit : AddPlantEvent()
}
```

- [ ] **Step 4: Create AddPlantState.kt**

```kotlin
package ai.lufious.app.presentation.garden.viewmodel

data class AddPlantState(
    val nickname: String = "",
    val species: String = "",
    val locationTag: String = "Living Room",
    val wateringIntervalDays: String = "7",
    val isLoading: Boolean = false,
    val isSubmitEnabled: Boolean = false
)
```

- [ ] **Step 5: Create AddPlantViewModel.kt**

```kotlin
package ai.lufious.app.presentation.garden.viewmodel

import ai.lufious.app.core.utils.BaseViewModel
import ai.lufious.app.core.utils.DispatcherProvider
import ai.lufious.app.core.utils.Result
import ai.lufious.app.core.utils.UiEffect
import ai.lufious.app.presentation.garden.data.usecases.AddPlantUseCase
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddPlantViewModel @Inject constructor(
    private val addPlant: AddPlantUseCase,
    dispatchers: DispatcherProvider
) : BaseViewModel<AddPlantEvent, AddPlantState>(AddPlantState(), dispatchers) {

    fun onEvent(event: AddPlantEvent) {
        viewModelScope.launch { handleEvent(event) }
    }

    override suspend fun handleEvent(event: AddPlantEvent) {
        when (event) {
            is AddPlantEvent.NicknameChanged -> {
                val canSubmit = event.value.isNotBlank() &&
                    state.value.species.isNotBlank() &&
                    (state.value.wateringIntervalDays.toIntOrNull() ?: 0) > 0
                setState { copy(nickname = event.value, isSubmitEnabled = canSubmit) }
            }
            is AddPlantEvent.SpeciesChanged -> {
                val canSubmit = state.value.nickname.isNotBlank() &&
                    event.value.isNotBlank() &&
                    (state.value.wateringIntervalDays.toIntOrNull() ?: 0) > 0
                setState { copy(species = event.value, isSubmitEnabled = canSubmit) }
            }
            is AddPlantEvent.LocationChanged ->
                setState { copy(locationTag = event.value) }
            is AddPlantEvent.WateringIntervalChanged -> {
                val canSubmit = state.value.nickname.isNotBlank() &&
                    state.value.species.isNotBlank() &&
                    (event.value.toIntOrNull() ?: 0) > 0
                setState { copy(wateringIntervalDays = event.value, isSubmitEnabled = canSubmit) }
            }
            AddPlantEvent.Submit -> {
                val days = state.value.wateringIntervalDays.toIntOrNull() ?: return
                setState { copy(isLoading = true) }
                ioLaunch {
                    when (val result = addPlant(
                        state.value.nickname,
                        state.value.species,
                        state.value.locationTag,
                        days
                    )) {
                        is Result.Success -> emitEffect(UiEffect.Navigate("back"))
                        is Result.Error -> {
                            setState { copy(isLoading = false) }
                            emitEffect(UiEffect.ShowError(result.message ?: "Failed to add plant"))
                        }
                    }
                }
            }
        }
    }
}
```

- [ ] **Step 6: Create AddPlantScreen.kt**

```kotlin
package ai.lufious.app.presentation.garden.ui

import ai.lufious.app.core.theme.Background
import ai.lufious.app.core.theme.PrimaryColor
import ai.lufious.app.core.utils.BACK_BUTTON_HEIGHT_FRACTION
import ai.lufious.app.core.utils.ShadowButton
import ai.lufious.app.core.utils.UiEffect
import ai.lufious.app.core.utils.hR
import ai.lufious.app.core.utils.rememberResponsiveDimensions
import ai.lufious.app.core.utils.wR
import ai.lufious.app.presentation.garden.viewmodel.AddPlantEvent
import ai.lufious.app.presentation.garden.viewmodel.AddPlantViewModel
import ai.lufious.app.presentation.utils.CommonTextField
import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest

private val locationOptions = listOf("Living Room", "Balcony", "Bedroom", "Office", "Outdoor")

@Composable
fun AddPlantScreen(
    navController: NavController,
    viewModel: AddPlantViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val dimensions = rememberResponsiveDimensions()

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is UiEffect.Navigate -> navController.popBackStack()
                is UiEffect.ShowError ->
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
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
                    text = "Add Plant",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = dimensions.wR(8f).dp, vertical = dimensions.hR(8f).dp)
                .testTag("add_plant_screen"),
            contentAlignment = Alignment.BottomCenter
        ) {
            Card(
                modifier = Modifier
                    .height(dimensions.heightFraction(0.78f).dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colors.onBackground)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "New Plant",
                            color = Color(0xFFB0AFFF),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        CommonTextField(
                            value = state.nickname,
                            onValueChange = { viewModel.onEvent(AddPlantEvent.NicknameChanged(it)) },
                            responsive = dimensions,
                            placeholder = "Plant nickname",
                            imeAction = ImeAction.Next,
                            onImeAction = {},
                            modifier = Modifier.testTag("nickname_field")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        CommonTextField(
                            value = state.species,
                            onValueChange = { viewModel.onEvent(AddPlantEvent.SpeciesChanged(it)) },
                            responsive = dimensions,
                            placeholder = "Species name",
                            imeAction = ImeAction.Next,
                            onImeAction = {},
                            modifier = Modifier.testTag("species_field")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Location dropdown
                        var expanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            TextField(
                                value = state.locationTag,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Location", color = Color.White.copy(alpha = 0.6f)) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.White.copy(alpha = 0.1f),
                                    unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                locationOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(text = option) },
                                        onClick = {
                                            viewModel.onEvent(AddPlantEvent.LocationChanged(option))
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        CommonTextField(
                            value = state.wateringIntervalDays,
                            onValueChange = {
                                viewModel.onEvent(AddPlantEvent.WateringIntervalChanged(it))
                            },
                            responsive = dimensions,
                            placeholder = "Water every N days",
                            imeAction = ImeAction.Done,
                            onImeAction = { viewModel.onEvent(AddPlantEvent.Submit) }
                        )
                    }

                    ShadowButton(
                        text = "ADD PLANT",
                        onClick = { viewModel.onEvent(AddPlantEvent.Submit) },
                        enabled = state.isSubmitEnabled && !state.isLoading,
                        isLoading = state.isLoading,
                        responsive = dimensions,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    )
                }
            }
        }
    }
}
```

- [ ] **Step 7: Run tests — confirm they pass**

```bash
./gradlew :app:connectedDevDebugAndroidTest \
  --tests "ai.lufious.app.presentation.garden.ui.AddPlantScreenTest" 2>&1 | tail -20
```

Expected: 2 tests PASS.

- [ ] **Step 8: Commit**

```bash
git add app/src/main/java/ai/lufious/app/presentation/garden/viewmodel/AddPlantEvent.kt \
        app/src/main/java/ai/lufious/app/presentation/garden/viewmodel/AddPlantState.kt \
        app/src/main/java/ai/lufious/app/presentation/garden/viewmodel/AddPlantViewModel.kt \
        app/src/main/java/ai/lufious/app/presentation/garden/ui/AddPlantScreen.kt \
        app/src/androidTest/java/ai/lufious/app/presentation/garden/ui/AddPlantScreenTest.kt
git commit -m "feat: add AddPlantViewModel and AddPlantScreen with form validation"
```

---

## Task 8: PlantDetailViewModel + PlantDetailScreen

**Files:**
- Create: `app/src/main/java/ai/lufious/app/presentation/garden/viewmodel/PlantDetailEvent.kt`
- Create: `app/src/main/java/ai/lufious/app/presentation/garden/viewmodel/PlantDetailState.kt`
- Create: `app/src/main/java/ai/lufious/app/presentation/garden/viewmodel/PlantDetailViewModel.kt`
- Create: `app/src/main/java/ai/lufious/app/presentation/garden/ui/PlantDetailScreen.kt`
- Create: `app/src/androidTest/java/ai/lufious/app/presentation/garden/ui/PlantDetailScreenTest.kt`

`PlantDetailViewModel` uses Compose Navigation's `SavedStateHandle` to get `plantId` from the nav argument automatically — no need to pass it explicitly.

- [ ] **Step 1: Write the failing test first**

Create `app/src/androidTest/java/ai/lufious/app/presentation/garden/ui/PlantDetailScreenTest.kt`:

```kotlin
package ai.lufious.app.presentation.garden.ui

import ai.lufious.app.HiltTestActivity
import ai.lufious.app.core.utils.Result
import ai.lufious.app.di.GardenModule
import ai.lufious.app.presentation.garden.data.models.CareLogModel
import ai.lufious.app.presentation.garden.data.models.PlantModel
import ai.lufious.app.presentation.garden.data.repository.PlantRepository
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Singleton

@HiltAndroidTest
@UninstallModules(GardenModule::class)
@RunWith(AndroidJUnit4::class)
class PlantDetailScreenTest {

    @Module
    @InstallIn(SingletonComponent::class)
    object FakeGardenModule {
        private val fakePlant = PlantModel(
            id = "plant1",
            nickname = "My Monstera",
            species = "Monstera Deliciosa",
            locationTag = "Living Room",
            wateringIntervalDays = 7,
            healthStatus = "healthy"
        )

        @Provides
        @Singleton
        fun providePlantRepository(): PlantRepository = object : PlantRepository {
            override suspend fun addPlant(
                nickname: String, species: String,
                locationTag: String, wateringIntervalDays: Int
            ) = Result.Success(PlantModel())
            override suspend fun getPlants() = Result.Success(emptyList<PlantModel>())
            override suspend fun getPlantById(plantId: String) = Result.Success(fakePlant)
            override suspend fun logCareAction(plantId: String, type: String, note: String) =
                Result.Success(CareLogModel(id = "log1", type = type, note = note))
            override suspend fun getCareLogs(plantId: String) = Result.Success(emptyList<CareLogModel>())
        }
    }

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun plantDetailScreen_rendersWithPlantInfo() {
        composeTestRule.setContent {
            PlantDetailScreen(navController = rememberNavController())
        }
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("plant_detail_screen")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("plant_detail_screen").assertIsDisplayed()
    }
}
```

- [ ] **Step 2: Run test to confirm it fails**

```bash
./gradlew :app:connectedDevDebugAndroidTest \
  --tests "ai.lufious.app.presentation.garden.ui.PlantDetailScreenTest" 2>&1 | tail -20
```

Expected: compilation error — `PlantDetailScreen` does not exist yet.

- [ ] **Step 3: Create PlantDetailEvent.kt**

```kotlin
package ai.lufious.app.presentation.garden.viewmodel

sealed class PlantDetailEvent {
    data class LogTypeSelected(val type: String) : PlantDetailEvent()
    data class NoteChanged(val note: String) : PlantDetailEvent()
    object ShowLogDialog : PlantDetailEvent()
    object DismissLogDialog : PlantDetailEvent()
    object SubmitLog : PlantDetailEvent()
}
```

- [ ] **Step 4: Create PlantDetailState.kt**

```kotlin
package ai.lufious.app.presentation.garden.viewmodel

import ai.lufious.app.presentation.garden.data.models.CareLogModel
import ai.lufious.app.presentation.garden.data.models.PlantModel

data class PlantDetailState(
    val plant: PlantModel? = null,
    val careLogs: List<CareLogModel> = emptyList(),
    val isLoading: Boolean = false,
    val showLogDialog: Boolean = false,
    val selectedLogType: String = "watered",
    val logNote: String = ""
)
```

- [ ] **Step 5: Create PlantDetailViewModel.kt**

```kotlin
package ai.lufious.app.presentation.garden.viewmodel

import ai.lufious.app.core.utils.BaseViewModel
import ai.lufious.app.core.utils.DispatcherProvider
import ai.lufious.app.core.utils.Result
import ai.lufious.app.core.utils.UiEffect
import ai.lufious.app.presentation.garden.data.usecases.GetCareLogsUseCase
import ai.lufious.app.presentation.garden.data.usecases.GetPlantByIdUseCase
import ai.lufious.app.presentation.garden.data.usecases.LogCareActionUseCase
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlantDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getPlantById: GetPlantByIdUseCase,
    private val logCareAction: LogCareActionUseCase,
    private val getCareLogs: GetCareLogsUseCase,
    dispatchers: DispatcherProvider
) : BaseViewModel<PlantDetailEvent, PlantDetailState>(PlantDetailState(), dispatchers) {

    private val plantId: String = savedStateHandle["plantId"] ?: ""

    init {
        loadPlant()
    }

    fun onEvent(event: PlantDetailEvent) {
        viewModelScope.launch { handleEvent(event) }
    }

    override suspend fun handleEvent(event: PlantDetailEvent) {
        when (event) {
            is PlantDetailEvent.LogTypeSelected ->
                setState { copy(selectedLogType = event.type) }
            is PlantDetailEvent.NoteChanged ->
                setState { copy(logNote = event.note) }
            PlantDetailEvent.ShowLogDialog ->
                setState { copy(showLogDialog = true) }
            PlantDetailEvent.DismissLogDialog ->
                setState { copy(showLogDialog = false, logNote = "", selectedLogType = "watered") }
            PlantDetailEvent.SubmitLog -> {
                setState { copy(isLoading = true) }
                ioLaunch {
                    when (val result = logCareAction(
                        plantId,
                        state.value.selectedLogType,
                        state.value.logNote
                    )) {
                        is Result.Success -> {
                            val updatedLogs = listOf(result.data!!) + state.value.careLogs
                            setState {
                                copy(
                                    careLogs = updatedLogs,
                                    isLoading = false,
                                    showLogDialog = false,
                                    logNote = "",
                                    selectedLogType = "watered"
                                )
                            }
                        }
                        is Result.Error -> {
                            setState { copy(isLoading = false) }
                            emitEffect(UiEffect.ShowError(result.message ?: "Failed to log action"))
                        }
                    }
                }
            }
        }
    }

    private fun loadPlant() {
        setState { copy(isLoading = true) }
        ioLaunch {
            when (val plantResult = getPlantById(plantId)) {
                is Result.Success -> setState { copy(plant = plantResult.data, isLoading = false) }
                is Result.Error -> setState { copy(isLoading = false) }
            }
            when (val logsResult = getCareLogs(plantId)) {
                is Result.Success -> setState { copy(careLogs = logsResult.data ?: emptyList()) }
                is Result.Error -> {}
            }
        }
    }
}
```

- [ ] **Step 6: Create PlantDetailScreen.kt**

```kotlin
package ai.lufious.app.presentation.garden.ui

import ai.lufious.app.core.theme.Background
import ai.lufious.app.core.theme.CriticalRed
import ai.lufious.app.core.theme.HealthyGreen
import ai.lufious.app.core.theme.PrimaryColor
import ai.lufious.app.core.theme.WarningOrange
import ai.lufious.app.core.utils.BACK_BUTTON_HEIGHT_FRACTION
import ai.lufious.app.core.utils.UiEffect
import ai.lufious.app.core.utils.hR
import ai.lufious.app.core.utils.rememberResponsiveDimensions
import ai.lufious.app.core.utils.wR
import ai.lufious.app.presentation.garden.data.models.CareLogModel
import ai.lufious.app.presentation.garden.viewmodel.PlantDetailEvent
import ai.lufious.app.presentation.garden.viewmodel.PlantDetailViewModel
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val logTypes = listOf("watered", "fertilized", "repotted", "note")

@Composable
fun PlantDetailScreen(
    navController: NavController,
    viewModel: PlantDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val dimensions = rememberResponsiveDimensions()

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is UiEffect.ShowError ->
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
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
                    text = state.plant?.nickname ?: "Plant Detail",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    ) { innerPadding ->
        when {
            state.isLoading && state.plant == null -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryColor)
            }

            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("plant_detail_screen"),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                state.plant?.let { plant ->
                    item {
                        // Plant info card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colors.onBackground
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                val healthColor = when (plant.healthStatus) {
                                    "warning" -> WarningOrange
                                    "critical" -> CriticalRed
                                    else -> HealthyGreen
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .background(
                                                color = HealthyGreen.copy(alpha = 0.15f),
                                                shape = RoundedCornerShape(12.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = plant.nickname.firstOrNull()
                                                ?.uppercaseChar()?.toString() ?: "🌱",
                                            fontSize = 22.sp,
                                            color = Color.White
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = plant.nickname,
                                            color = Color.White,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = plant.species,
                                            color = Color.White.copy(alpha = 0.6f),
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    InfoChip(label = "📍 ${plant.locationTag}")
                                    InfoChip(label = "💧 Every ${plant.wateringIntervalDays}d")
                                    InfoChip(label = healthColor.toStatusLabel())
                                }
                            }
                        }
                    }

                    item {
                        // Log care action button
                        Button(
                            onClick = { viewModel.onEvent(PlantDetailEvent.ShowLogDialog) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                        ) {
                            Text(
                                text = "LOG CARE ACTION",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (state.careLogs.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No care actions logged yet",
                                    color = Color.White.copy(alpha = 0.4f),
                                    fontSize = 13.sp
                                )
                            }
                        }
                    } else {
                        item {
                            Text(
                                text = "Care Log",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        items(state.careLogs, key = { it.id }) { log ->
                            CareLogItem(log = log)
                        }
                    }
                }
            }
        }
    }

    // Care log dialog
    if (state.showLogDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(PlantDetailEvent.DismissLogDialog) },
            title = {
                Text(
                    text = "Log Care Action",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Type",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        logTypes.forEach { type ->
                            val selected = state.selectedLogType == type
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = if (selected) PrimaryColor
                                        else Color.White.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        viewModel.onEvent(PlantDetailEvent.LogTypeSelected(type))
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = type.replaceFirstChar { it.uppercase() },
                                    color = Color.White,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = state.logNote,
                        onValueChange = { viewModel.onEvent(PlantDetailEvent.NoteChanged(it)) },
                        placeholder = {
                            Text(
                                "Note (optional)",
                                color = Color.White.copy(alpha = 0.4f)
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color.White.copy(alpha = 0.1f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                            focusedIndicatorColor = PrimaryColor,
                            unfocusedIndicatorColor = Color.White.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.onEvent(PlantDetailEvent.SubmitLog) },
                    enabled = !state.isLoading
                ) {
                    Text("LOG", color = PrimaryColor, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.onEvent(PlantDetailEvent.DismissLogDialog) }
                ) {
                    Text("CANCEL", color = Color.White.copy(alpha = 0.6f))
                }
            },
            containerColor = Color(0xFF1E2020)
        )
    }
}

@Composable
private fun InfoChip(label: String) {
    Box(
        modifier = Modifier
            .background(
                color = Color.White.copy(alpha = 0.12f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = label, color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
    }
}

@Composable
private fun CareLogItem(log: CareLogModel) {
    val dateStr = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
        .format(Date(log.timestamp))
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
        Icon(
            imageVector = when (log.type) {
                "watered" -> Icons.Default.WaterDrop
                else -> Icons.Default.LocalFlorist
            },
            contentDescription = null,
            tint = PrimaryColor,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = log.type.replaceFirstChar { it.uppercase() },
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            if (log.note.isNotBlank()) {
                Text(
                    text = log.note,
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 11.sp
                )
            }
            Text(
                text = dateStr,
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 10.sp
            )
        }
    }
}

private fun Color.toStatusLabel(): String = when (this) {
    WarningOrange -> "⚠ Warning"
    CriticalRed -> "🔴 Critical"
    else -> "✅ Healthy"
}
```

- [ ] **Step 7: Run tests — confirm they pass**

```bash
./gradlew :app:connectedDevDebugAndroidTest \
  --tests "ai.lufious.app.presentation.garden.ui.PlantDetailScreenTest" 2>&1 | tail -20
```

Expected: 1 test PASS.

- [ ] **Step 8: Commit**

```bash
git add app/src/main/java/ai/lufious/app/presentation/garden/viewmodel/PlantDetailEvent.kt \
        app/src/main/java/ai/lufious/app/presentation/garden/viewmodel/PlantDetailState.kt \
        app/src/main/java/ai/lufious/app/presentation/garden/viewmodel/PlantDetailViewModel.kt \
        app/src/main/java/ai/lufious/app/presentation/garden/ui/PlantDetailScreen.kt \
        app/src/androidTest/java/ai/lufious/app/presentation/garden/ui/PlantDetailScreenTest.kt
git commit -m "feat: add PlantDetailViewModel and PlantDetailScreen with care log"
```

---

## Task 9: Wire navigation in MainScreen

**Files:**
- Modify: `app/src/main/java/ai/lufious/app/presentation/main/ui/MainScreen.kt`

`AddPlantScreen` and `PlantDetailScreen` live in the inner NavHost (alongside the tabs). The bottom nav stays visible on sub-screens for Phase 2 (can be conditionally hidden in a later phase). `GardenPage` now receives `tabNavController` so it can navigate to these sub-screens.

- [ ] **Step 1: Update MainScreen.kt**

Full new content of `MainScreen.kt`:

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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

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

- [ ] **Step 2: Commit**

```bash
git add app/src/main/java/ai/lufious/app/presentation/main/ui/MainScreen.kt
git commit -m "feat: wire AddPlant and PlantDetail into MainScreen inner NavHost"
```

---

## Verification

After signing in, confirm on device/emulator:

- [ ] Garden tab shows "No plants yet" and a "+" FAB
- [ ] Tapping "+" navigates to Add Plant screen with: nickname field, species field, location dropdown, watering interval field
- [ ] Filling in nickname + species enables the "ADD PLANT" button
- [ ] Submitting a valid form saves to Firestore and navigates back to Garden tab
- [ ] Garden tab now shows the new plant as a card in the 2-column grid
- [ ] Tapping a plant card navigates to Plant Detail screen showing: nickname, species, location, watering interval
- [ ] "LOG CARE ACTION" button opens a dialog with type selection (Water / Fertilize / Repot / Note) + optional note
- [ ] Submitting the dialog appends the log to the care log list
- [ ] Back button on Add Plant and Plant Detail returns to Garden tab
