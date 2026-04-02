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
