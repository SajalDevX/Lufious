package ai.lufious.app.presentation.main.ui

import ai.lufious.app.core.theme.Background
import ai.lufious.app.core.utils.Screen
import ai.lufious.app.presentation.garden.ui.AddPlantScreen
import ai.lufious.app.presentation.garden.ui.GardenPage
import ai.lufious.app.presentation.garden.ui.PlantDetailScreen
import ai.lufious.app.presentation.home.ui.HomePage
import ai.lufious.app.presentation.scan.ui.ScanPage
import ai.lufious.app.presentation.shop.ui.CreateListingScreen
import ai.lufious.app.presentation.shop.ui.ListingDetailScreen
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
            composable(Screen.ShopTab.route) {
                ShopPage(navController = tabNavController)
            }
            composable(Screen.AddPlant.route) {
                AddPlantScreen(navController = tabNavController)
            }
            composable(
                route = Screen.PlantDetail.route,
                arguments = listOf(navArgument("plantId") { type = NavType.StringType })
            ) {
                PlantDetailScreen(navController = tabNavController)
            }
            composable(Screen.CreateListing.route) {
                CreateListingScreen(navController = tabNavController)
            }
            composable(
                route = Screen.ListingDetail.route,
                arguments = listOf(navArgument("listingId") { type = NavType.StringType })
            ) {
                ListingDetailScreen(navController = tabNavController)
            }
        }
    }
}
