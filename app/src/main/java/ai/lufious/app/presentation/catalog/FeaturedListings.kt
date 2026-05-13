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
        title = "Monstera Deliciosa — 4ft",
        description = "Healthy split-leaf monstera, 4ft tall. Includes ceramic pot. Local pickup only.",
        priceCents = 4500_00,
        currency = "INR",
        category = "indoor",
        photoUrl = "https://images.unsplash.com/photo-1614594975525-e45190c55d0b?w=600",
        sellerName = "Riya G."
    ),
    DemoListing(
        id = "demo-2",
        title = "Snake Plant trio",
        description = "Three healthy snake plants in terracotta pots. Beginner-friendly.",
        priceCents = 1200_00,
        currency = "INR",
        category = "indoor",
        photoUrl = "https://images.unsplash.com/photo-1593482892290-f54927ae1bb6?w=600",
        sellerName = "Arjun M."
    ),
    DemoListing(
        id = "demo-3",
        title = "Aloe vera pups (set of 5)",
        description = "Five rooted aloe pups, ready to plant. Great for kitchen window.",
        priceCents = 600_00,
        currency = "INR",
        category = "succulent",
        photoUrl = "https://images.unsplash.com/photo-1509223197845-458d87318791?w=600",
        sellerName = "Priya S."
    ),
    DemoListing(
        id = "demo-4",
        title = "Echeveria rosettes",
        description = "Assorted echeveria in 3-inch pots. Sun-friendly.",
        priceCents = 350_00,
        currency = "INR",
        category = "succulent",
        photoUrl = "https://images.unsplash.com/photo-1459411552884-841db9b3cc2a?w=600",
        sellerName = "Kabir T."
    ),
    DemoListing(
        id = "demo-5",
        title = "Basil starter kit",
        description = "Three basil seedlings + organic potting mix. Ready to grow.",
        priceCents = 450_00,
        currency = "INR",
        category = "herb",
        photoUrl = "https://images.unsplash.com/photo-1618375569909-3c8616cf7733?w=600",
        sellerName = "Nisha K."
    ),
    DemoListing(
        id = "demo-6",
        title = "Heirloom tomato seedlings",
        description = "Six varieties of heirloom tomatoes, 6-week-old seedlings.",
        priceCents = 800_00,
        currency = "INR",
        category = "vegetable",
        photoUrl = "https://images.unsplash.com/photo-1592841200221-a6898f307baa?w=600",
        sellerName = "Vikram J."
    ),
    DemoListing(
        id = "demo-7",
        title = "Phalaenopsis orchid — white",
        description = "Blooming white orchid in display pot. Lasts months.",
        priceCents = 1800_00,
        currency = "INR",
        category = "flower",
        photoUrl = "https://images.unsplash.com/photo-1567696911980-2eed69a46042?w=600",
        sellerName = "Asha P."
    ),
    DemoListing(
        id = "demo-8",
        title = "Rose bush — David Austin",
        description = "Pink David Austin rose, 2 years old. Heavy bloomer.",
        priceCents = 2200_00,
        currency = "INR",
        category = "flower",
        photoUrl = "https://images.unsplash.com/photo-1463320726281-696a485928c7?w=600",
        sellerName = "Meera L."
    ),
    DemoListing(
        id = "demo-9",
        title = "Pothos cuttings (10 stems)",
        description = "Rooted golden pothos cuttings, ready to pot.",
        priceCents = 300_00,
        currency = "INR",
        category = "indoor",
        photoUrl = "https://images.unsplash.com/photo-1572688484438-313a6e50c333?w=600",
        sellerName = "Tara V."
    ),
    DemoListing(
        id = "demo-10",
        title = "Rosemary bush",
        description = "Mature rosemary, perfect for cooking. 1ft tall.",
        priceCents = 550_00,
        currency = "INR",
        category = "herb",
        photoUrl = "https://images.unsplash.com/photo-1515426954-9d8b41ce28e1?w=600",
        sellerName = "Devan R."
    ),
    DemoListing(
        id = "demo-11",
        title = "Strawberry plants (pack of 6)",
        description = "Ever-bearing strawberries. Crop within 8 weeks.",
        priceCents = 700_00,
        currency = "INR",
        category = "vegetable",
        photoUrl = "https://images.unsplash.com/photo-1518635017498-87f514b751ba?w=600",
        sellerName = "Sana A."
    ),
    DemoListing(
        id = "demo-12",
        title = "Lavender — English",
        description = "Fragrant lavender, attracts pollinators. Drought-tolerant.",
        priceCents = 650_00,
        currency = "INR",
        category = "flower",
        photoUrl = "https://images.unsplash.com/photo-1499002238440-d264edd596ec?w=600",
        sellerName = "Rohit B."
    )
)
