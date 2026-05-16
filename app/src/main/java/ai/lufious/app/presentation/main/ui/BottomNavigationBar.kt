package ai.lufious.app.presentation.main.ui

import ai.lufious.app.core.theme.PrimaryColor
import ai.lufious.app.core.theme.Surface
import ai.lufious.app.core.utils.Screen
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import ai.lufious.app.core.theme.TextPrimary
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun BottomNavigationBar(
    navController: NavController,
    onProfileClick: () -> Unit = {}
) {
    val tabs = listOf(
        BottomNavItem("Home", Icons.Default.Home, Screen.HomeTab.route),
        BottomNavItem("Scan", Icons.Default.PhotoCamera, Screen.ScanTab.route),
        BottomNavItem("Garden", Icons.Default.LocalFlorist, Screen.GardenTab.route),
        BottomNavItem("Shop", Icons.Default.ShoppingBag, Screen.ShopTab.route),
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(86.dp),
        containerColor = Surface,
        contentColor = TextPrimary,
        windowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        tabs.forEach { item ->
            NavigationBarItem(
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
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryColor,
                    selectedTextColor = PrimaryColor,
                    unselectedIconColor = TextPrimary.copy(alpha = 0.5f),
                    unselectedTextColor = TextPrimary.copy(alpha = 0.5f),
                    indicatorColor = PrimaryColor.copy(alpha = 0.18f)
                )
            )
        }
        NavigationBarItem(
            selected = false,
            onClick = onProfileClick,
            icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Profile") },
            label = { Text(text = "Profile", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryColor,
                selectedTextColor = PrimaryColor,
                unselectedIconColor = TextPrimary.copy(alpha = 0.5f),
                unselectedTextColor = TextPrimary.copy(alpha = 0.5f),
                indicatorColor = PrimaryColor.copy(alpha = 0.18f)
            )
        )
    }
}
