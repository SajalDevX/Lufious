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
        fun createRoute(plantId: String) = route.replace("{plantId}", plantId)
    }

    // Shop sub-screens — still within MainScreen's inner NavHost
    object CreateListing : Screen("shop/create_listing")
    object ListingDetail : Screen("shop/listing/{listingId}") {
        fun createRoute(listingId: String) = route.replace("{listingId}", listingId)
    }

    // Scan sub-screens — still within MainScreen's inner NavHost
    object ScanResult : Screen("scan/result/{scanId}") {
        fun createRoute(scanId: String) = route.replace("{scanId}", scanId)
    }

    // AddPlant with optional species pre-fill (from Scan)
    object AddPlantWithSpecies {
        fun createRoute(species: String) =
            "garden/add_plant?species=${android.net.Uri.encode(species)}"
    }
}

const val AUTH_GRAPH = "auth_graph"
const val MAIN_GRAPH = "main_graph"
