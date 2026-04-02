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
        composeTestRule.onNodeWithTag("home_screen").assertIsDisplayed()
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
