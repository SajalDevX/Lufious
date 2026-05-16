package ai.lufious.app.presentation.catalog

data class DemoListing(
    val id: String,
    val title: String,
    val description: String,
    val priceCents: Int,
    val currency: String,
    val category: String,
    val photoUrl: String,
    val sellerName: String,
    val isDemo: Boolean = true
)

val featuredListings: List<DemoListing> = listOf(
    DemoListing(
        id = "demo-1",
        title = "Monstera",
        description = "Healthy split-leaf monstera, 4ft tall. Includes ceramic pot.",
        priceCents = 785,
        currency = "USD",
        category = "houseplant",
        photoUrl = "https://images.unsplash.com/photo-1614594975525-e45190c55d0b?w=600",
        sellerName = "Riya G."
    ),
    DemoListing(
        id = "demo-2",
        title = "Pilea Peperomia",
        description = "Cute pancake plant in a decorative pot. Easy care.",
        priceCents = 400,
        currency = "USD",
        category = "houseplant",
        photoUrl = "https://images.unsplash.com/photo-1552473927-bc2b8ab1b98a?w=600",
        sellerName = "Arjun M."
    ),
    DemoListing(
        id = "demo-3",
        title = "Natal Mahogany Tree",
        description = "Elegant indoor tree, lush dark green foliage.",
        priceCents = 1734,
        currency = "USD",
        category = "houseplant",
        photoUrl = "https://images.unsplash.com/photo-1585435557343-3b092031a831?w=600",
        sellerName = "Priya S."
    ),
    DemoListing(
        id = "demo-4",
        title = "Snake Plant",
        description = "Three healthy snake plants in terracotta pots. Beginner-friendly.",
        priceCents = 1200,
        currency = "USD",
        category = "houseplant",
        photoUrl = "https://images.unsplash.com/photo-1593482892290-f54927ae1bb6?w=600",
        sellerName = "Kabir T."
    ),
    DemoListing(
        id = "demo-5",
        title = "Peace Lily",
        description = "Elegant white blooms, air purifying. Low light tolerant.",
        priceCents = 950,
        currency = "USD",
        category = "flowering",
        photoUrl = "https://images.unsplash.com/photo-1593691509543-c55fb32d8de5?w=600",
        sellerName = "Nisha K."
    ),
    DemoListing(
        id = "demo-6",
        title = "Fiddle Leaf Fig",
        description = "Large dramatic leaves, statement indoor tree.",
        priceCents = 2200,
        currency = "USD",
        category = "houseplant",
        photoUrl = "https://images.unsplash.com/photo-1597055181300-0f6c295b0f11?w=600",
        sellerName = "Vikram J."
    ),
    DemoListing(
        id = "demo-7",
        title = "Aloe Vera",
        description = "Five rooted aloe pups, ready to plant. Great for windowsill.",
        priceCents = 600,
        currency = "USD",
        category = "perennial",
        photoUrl = "https://images.unsplash.com/photo-1509223197845-458d87318791?w=600",
        sellerName = "Asha P."
    ),
    DemoListing(
        id = "demo-8",
        title = "Pothos",
        description = "Rooted golden pothos cuttings, trailing vine. Very easy to grow.",
        priceCents = 300,
        currency = "USD",
        category = "houseplant",
        photoUrl = "https://images.unsplash.com/photo-1572688484438-313a6e50c333?w=600",
        sellerName = "Meera L."
    ),
    DemoListing(
        id = "demo-9",
        title = "Japanese Juniper",
        description = "Beautiful conifer bonsai style. 3 years old.",
        priceCents = 3500,
        currency = "USD",
        category = "conifers",
        photoUrl = "https://images.unsplash.com/photo-1467453678174-768ec283a940?w=600",
        sellerName = "Tara V."
    ),
    DemoListing(
        id = "demo-10",
        title = "Lavender",
        description = "Fragrant English lavender. Attracts pollinators.",
        priceCents = 650,
        currency = "USD",
        category = "perennial",
        photoUrl = "https://images.unsplash.com/photo-1499002238440-d264edd596ec?w=600",
        sellerName = "Devan R."
    ),
    DemoListing(
        id = "demo-11",
        title = "Boxwood Shrub",
        description = "Classic evergreen shrub. Great for hedges or pots.",
        priceCents = 1800,
        currency = "USD",
        category = "shrubs",
        photoUrl = "https://images.unsplash.com/photo-1416879595882-3373a0480b5b?w=600",
        sellerName = "Sana A."
    ),
    DemoListing(
        id = "demo-12",
        title = "Bird of Paradise",
        description = "Dramatic tropical foliage. Bold statement plant.",
        priceCents = 2800,
        currency = "USD",
        category = "flowering",
        photoUrl = "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=600",
        sellerName = "Rohit B."
    )
)
