package ai.lufious.app.presentation.catalog

data class ScanExample(
    val id: String,
    val plantName: String,
    val commonName: String,
    val photoUrl: String,
    val confidence: Float,
    val issue: String?,
    val advice: String
)

val scanExamples: List<ScanExample> = listOf(
    ScanExample(
        id = "scan-ex-1",
        plantName = "Monstera deliciosa",
        commonName = "Swiss Cheese Plant",
        photoUrl = "https://images.unsplash.com/photo-1614594975525-e45190c55d0b?w=600",
        confidence = 0.96f,
        issue = null,
        advice = "Healthy. Continue with bright indirect light and weekly watering."
    ),
    ScanExample(
        id = "scan-ex-2",
        plantName = "Sansevieria trifasciata",
        commonName = "Snake Plant",
        photoUrl = "https://images.unsplash.com/photo-1593482892290-f54927ae1bb6?w=600",
        confidence = 0.92f,
        issue = "Slight overwatering",
        advice = "Let soil fully dry. Reduce watering frequency to every 2-3 weeks."
    ),
    ScanExample(
        id = "scan-ex-3",
        plantName = "Solanum lycopersicum",
        commonName = "Tomato",
        photoUrl = "https://images.unsplash.com/photo-1592841200221-a6898f307baa?w=600",
        confidence = 0.94f,
        issue = "Early blight on lower leaves",
        advice = "Remove affected leaves. Improve airflow and avoid wetting foliage."
    ),
    ScanExample(
        id = "scan-ex-4",
        plantName = "Rosa hybrid",
        commonName = "Rose",
        photoUrl = "https://images.unsplash.com/photo-1463320726281-696a485928c7?w=600",
        confidence = 0.89f,
        issue = "Black spot fungus",
        advice = "Prune infected canes. Apply neem oil weekly until clear."
    ),
    ScanExample(
        id = "scan-ex-5",
        plantName = "Epipremnum aureum",
        commonName = "Golden Pothos",
        photoUrl = "https://images.unsplash.com/photo-1572688484438-313a6e50c333?w=600",
        confidence = 0.97f,
        issue = null,
        advice = "Healthy and trailing well. Trim a long vine to propagate."
    ),
    ScanExample(
        id = "scan-ex-6",
        plantName = "Aloe barbadensis",
        commonName = "Aloe Vera",
        photoUrl = "https://images.unsplash.com/photo-1509223197845-458d87318791?w=600",
        confidence = 0.95f,
        issue = "Leaf curling — underwatering",
        advice = "Give a deep soak. Wait for soil to fully dry before next watering."
    ),
    ScanExample(
        id = "scan-ex-7",
        plantName = "Ocimum basilicum",
        commonName = "Basil",
        photoUrl = "https://images.unsplash.com/photo-1618375569909-3c8616cf7733?w=600",
        confidence = 0.93f,
        issue = "Flowering — flavor decline",
        advice = "Pinch off flower buds to keep leaves tasty and bushy."
    ),
    ScanExample(
        id = "scan-ex-8",
        plantName = "Phalaenopsis amabilis",
        commonName = "Moth Orchid",
        photoUrl = "https://images.unsplash.com/photo-1567696911980-2eed69a46042?w=600",
        confidence = 0.91f,
        issue = null,
        advice = "Healthy. Water by ice cubes once weekly. Bright indirect light."
    )
)
