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
