package ai.lufious.app.presentation.shop.ui

import ai.lufious.app.core.utils.Screen
import ai.lufious.app.core.utils.UiEffect
import ai.lufious.app.presentation.catalog.DemoListing
import ai.lufious.app.presentation.catalog.featuredListings
import ai.lufious.app.presentation.shop.data.models.ListingCategory
import ai.lufious.app.presentation.shop.viewmodel.ShopEvent
import ai.lufious.app.presentation.shop.viewmodel.ShopViewModel
import coil.compose.AsyncImage
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest

private val ShopBg = Color(0xFF0D2217)
private val ShopBgCard = Color(0xFF163320)
private val SearchBarBg = Color(0xFF1A3A27)
private val ShopCardBg = Color.White
private val PlusGreen = Color(0xFF1A5C35)
private val ViewGreen = Color(0xFF22A060)
private val TabSelectedColor = Color.White
private val TabUnselectedColor = Color.White.copy(alpha = 0.40f)
private val ShopTextDark = Color(0xFF0F1F14)

@Composable
fun ShopPage(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: ShopViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is UiEffect.ShowError ->
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                else -> {}
            }
        }
    }

    val demoListings = remember(state.selectedCategory) {
        val sel = state.selectedCategory
        if (sel.equals(ListingCategory.ALL, ignoreCase = true)) featuredListings
        else featuredListings
            .filter { it.category.equals(sel, ignoreCase = true) }
            .ifEmpty { featuredListings }
    }

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = modifier
            .fillMaxSize()
            .background(ShopBg)
            .testTag("shop_screen"),
        contentPadding = PaddingValues(bottom = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalItemSpacing = 10.dp
    ) {
        // Top bar
        item(span = StaggeredGridItemSpan.FullLine) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Icon(
                    imageVector = Icons.Default.ShoppingBag,
                    contentDescription = "Cart",
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            navController.navigate(Screen.CreateListing.route)
                        }
                )
            }
        }

        // Search bar
        item(span = StaggeredGridItemSpan.FullLine) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(SearchBarBg)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "Search here",
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 14.sp
                )
            }
        }

        // Category tabs
        item(span = StaggeredGridItemSpan.FullLine) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                items(ListingCategory.browseOptions) { category ->
                    val selected = state.selectedCategory.equals(category, ignoreCase = true)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            viewModel.onEvent(ShopEvent.CategoryChanged(category))
                        }
                    ) {
                        Text(
                            text = ListingCategory.displayName(category),
                            color = if (selected) TabSelectedColor else TabUnselectedColor,
                            fontSize = 14.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                        )
                        if (selected) {
                            Spacer(Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .width(20.dp)
                                    .height(2.dp)
                                    .clip(RoundedCornerShape(1.dp))
                                    .background(Color.White)
                            )
                        } else {
                            Spacer(Modifier.height(6.dp))
                        }
                    }
                }
            }
        }

        // Spacer between tabs and grid
        item(span = StaggeredGridItemSpan.FullLine) {
            Spacer(Modifier.height(4.dp))
        }

        // Product cards — staggered grid
        if (state.listings.isEmpty()) {
            items(
                items = demoListings,
                key = { it.id }
            ) { listing ->
                ProductCard(
                    listing = listing,
                    onClick = {
                        Toast.makeText(context, "Marketplace launching soon", Toast.LENGTH_SHORT)
                            .show()
                    }
                )
            }
        } else {
            items(
                items = state.listings,
                key = { it.id }
            ) { listing ->
                ListingCard(
                    listing = listing,
                    isWishlisted = listing.id in state.wishlistIds,
                    onClick = {
                        navController.navigate(Screen.ListingDetail.createRoute(listing.id))
                    },
                    onToggleWishlist = {
                        viewModel.onEvent(ShopEvent.ToggleWishlist(listing.id))
                    }
                )
            }
        }
    }
}

@Composable
private fun ProductCard(listing: DemoListing, onClick: () -> Unit) {
    val aspectRatio = remember(listing.id) {
        // Vary image height per card for staggered effect
        if (listing.id.hashCode() % 2 == 0) 0.72f else 1.05f
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(ShopCardBg)
            .clickable { onClick() }
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = listing.photoUrl,
                    contentDescription = listing.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(aspectRatio)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                )
                // "+" button
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(PlusGreen)
                        .clickable { onClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = listing.title,
                    color = ShopTextDark,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$${listing.priceCents}",
                        color = ShopTextDark,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "View details",
                        color = ViewGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
