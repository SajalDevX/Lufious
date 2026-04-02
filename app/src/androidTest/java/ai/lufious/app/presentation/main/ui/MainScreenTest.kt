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
