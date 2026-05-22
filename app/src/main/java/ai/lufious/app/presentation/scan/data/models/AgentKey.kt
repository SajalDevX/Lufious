package ai.lufious.app.presentation.scan.data.models

import androidx.compose.ui.graphics.Color

enum class AgentKey(
    val wireValue: String,
    val title: String,
    val emoji: String,
    val tagline: String,
    val accent: Color,
    val cardBg: Color
) {
    SOIL(
        wireValue = "soil",
        title = "Soil & Fertility",
        emoji = "🌱",
        tagline = "Moisture, nutrients, drainage",
        accent = Color(0xFF8B5E3C),
        cardBg = Color(0xFFFFF6E9)
    ),
    DISEASE(
        wireValue = "disease",
        title = "Disease & Pests",
        emoji = "🦠",
        tagline = "Spots, fungi, bugs, rot",
        accent = Color(0xFFC0392B),
        cardBg = Color(0xFFFFEDEA)
    ),
    SOLAR(
        wireValue = "solar",
        title = "Light & Sun",
        emoji = "☀️",
        tagline = "Exposure, placement, scorch",
        accent = Color(0xFFE0A20A),
        cardBg = Color(0xFFFFF8DD)
    ),
    CARE(
        wireValue = "care",
        title = "Care & Growth",
        emoji = "🌿",
        tagline = "ID, vigour, 30-day plan",
        accent = Color(0xFF138A45),
        cardBg = Color(0xFFE9F7E7)
    );

    companion object {
        fun fromWire(value: String?): AgentKey? =
            values().firstOrNull { it.wireValue == value }
    }
}
