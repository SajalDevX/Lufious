package ai.lufious.app.presentation.catalog

data class PlantSpecies(
    val id: String,
    val name: String,
    val emoji: String,
    val difficulty: Difficulty,
    val light: Light,
    val water: Water,
    val category: String,
    val careTips: List<String>
) {
    enum class Difficulty { Beginner, Intermediate, Expert }
    enum class Light { Low, Medium, Bright, Direct }
    enum class Water { Sparse, Weekly, Frequent }
}

val plantCatalog: List<PlantSpecies> = listOf(
    PlantSpecies("snake-plant", "Snake Plant", "🐍", PlantSpecies.Difficulty.Beginner, PlantSpecies.Light.Low, PlantSpecies.Water.Sparse, "indoor",
        listOf("Tolerates neglect. Water every 2-3 weeks.", "Avoid overwatering — roots rot easily.", "Thrives in low to bright indirect light.")),
    PlantSpecies("pothos", "Pothos", "🌿", PlantSpecies.Difficulty.Beginner, PlantSpecies.Light.Medium, PlantSpecies.Water.Weekly, "indoor",
        listOf("Trailing vine — looks great on shelves.", "Let top inch of soil dry between waterings.", "Trim leggy stems to encourage bushiness.")),
    PlantSpecies("monstera", "Monstera Deliciosa", "🍃", PlantSpecies.Difficulty.Intermediate, PlantSpecies.Light.Bright, PlantSpecies.Water.Weekly, "indoor",
        listOf("Wants bright indirect light.", "Mist leaves weekly for humidity.", "Stake it as it grows for support.")),
    PlantSpecies("zz-plant", "ZZ Plant", "🌱", PlantSpecies.Difficulty.Beginner, PlantSpecies.Light.Low, PlantSpecies.Water.Sparse, "indoor",
        listOf("Forgives forgetful owners.", "Wipe leaves to keep them shiny.", "Water only when soil fully dry.")),
    PlantSpecies("fiddle-leaf-fig", "Fiddle Leaf Fig", "🌳", PlantSpecies.Difficulty.Expert, PlantSpecies.Light.Bright, PlantSpecies.Water.Weekly, "indoor",
        listOf("Pick a spot and don't move it — hates change.", "Bright indirect light is key.", "Rotate weekly for even growth.")),
    PlantSpecies("peace-lily", "Peace Lily", "🤍", PlantSpecies.Difficulty.Beginner, PlantSpecies.Light.Low, PlantSpecies.Water.Weekly, "indoor",
        listOf("Droops dramatically when thirsty — easy to read.", "White blooms when happy.", "Keep soil lightly moist.")),
    PlantSpecies("spider-plant", "Spider Plant", "🕷️", PlantSpecies.Difficulty.Beginner, PlantSpecies.Light.Medium, PlantSpecies.Water.Weekly, "indoor",
        listOf("Produces babies you can propagate.", "Pet-safe.", "Bright indirect light grows best.")),
    PlantSpecies("rubber-plant", "Rubber Plant", "🌴", PlantSpecies.Difficulty.Intermediate, PlantSpecies.Light.Bright, PlantSpecies.Water.Weekly, "indoor",
        listOf("Wipe leaves monthly.", "Let soil dry between waterings.", "Bright indirect light.")),
    PlantSpecies("philodendron", "Philodendron", "💚", PlantSpecies.Difficulty.Beginner, PlantSpecies.Light.Medium, PlantSpecies.Water.Weekly, "indoor",
        listOf("Heart-shaped leaves love medium light.", "Trim for bushier growth.", "Water when top inch dries.")),
    PlantSpecies("aloe-vera", "Aloe Vera", "🌵", PlantSpecies.Difficulty.Beginner, PlantSpecies.Light.Bright, PlantSpecies.Water.Sparse, "succulent",
        listOf("Soothing gel — kitchen-friendly.", "Bright light, water sparingly.", "Use well-draining soil.")),
    PlantSpecies("jade-plant", "Jade Plant", "💎", PlantSpecies.Difficulty.Beginner, PlantSpecies.Light.Direct, PlantSpecies.Water.Sparse, "succulent",
        listOf("Symbol of prosperity.", "Direct light grows thick leaves.", "Water deeply but rarely.")),
    PlantSpecies("echeveria", "Echeveria", "🌸", PlantSpecies.Difficulty.Intermediate, PlantSpecies.Light.Direct, PlantSpecies.Water.Sparse, "succulent",
        listOf("Rosette form — gorgeous in pots.", "Direct sun makes colors pop.", "Avoid water on leaves.")),
    PlantSpecies("string-of-pearls", "String of Pearls", "📿", PlantSpecies.Difficulty.Intermediate, PlantSpecies.Light.Bright, PlantSpecies.Water.Sparse, "succulent",
        listOf("Trailing succulent — hang it.", "Drains fast — use cactus soil.", "Don't overwater.")),
    PlantSpecies("haworthia", "Haworthia", "🦓", PlantSpecies.Difficulty.Beginner, PlantSpecies.Light.Medium, PlantSpecies.Water.Sparse, "succulent",
        listOf("Compact and slow-growing.", "Tolerates lower light than most succulents.", "Water every 2-3 weeks.")),
    PlantSpecies("basil", "Basil", "🌿", PlantSpecies.Difficulty.Beginner, PlantSpecies.Light.Direct, PlantSpecies.Water.Frequent, "herb",
        listOf("Pinch tops to keep bushy.", "Loves a sunny windowsill.", "Water when soil feels dry.")),
    PlantSpecies("mint", "Mint", "🍃", PlantSpecies.Difficulty.Beginner, PlantSpecies.Light.Bright, PlantSpecies.Water.Frequent, "herb",
        listOf("Aggressive grower — keep in own pot.", "Snip leaves often for fresher growth.", "Keep soil consistently moist.")),
    PlantSpecies("rosemary", "Rosemary", "🌾", PlantSpecies.Difficulty.Intermediate, PlantSpecies.Light.Direct, PlantSpecies.Water.Weekly, "herb",
        listOf("Mediterranean — loves sun.", "Let soil dry between waterings.", "Good drainage critical.")),
    PlantSpecies("thyme", "Thyme", "🌱", PlantSpecies.Difficulty.Beginner, PlantSpecies.Light.Direct, PlantSpecies.Water.Weekly, "herb",
        listOf("Tough little herb.", "Direct sun preferred.", "Tolerates dry soil better than wet.")),
    PlantSpecies("cilantro", "Cilantro", "🌿", PlantSpecies.Difficulty.Intermediate, PlantSpecies.Light.Bright, PlantSpecies.Water.Weekly, "herb",
        listOf("Bolts in heat — grow cool season.", "Snip outer leaves first.", "Succession-sow for steady supply.")),
    PlantSpecies("parsley", "Parsley", "🍀", PlantSpecies.Difficulty.Beginner, PlantSpecies.Light.Bright, PlantSpecies.Water.Weekly, "herb",
        listOf("Biennial — replace yearly.", "Keep soil moist.", "Tolerates partial shade.")),
    PlantSpecies("tomato", "Tomato", "🍅", PlantSpecies.Difficulty.Intermediate, PlantSpecies.Light.Direct, PlantSpecies.Water.Frequent, "vegetable",
        listOf("Stake or cage early.", "Deep watering at base, not leaves.", "Pinch suckers for indeterminate varieties.")),
    PlantSpecies("pepper", "Bell Pepper", "🫑", PlantSpecies.Difficulty.Intermediate, PlantSpecies.Light.Direct, PlantSpecies.Water.Weekly, "vegetable",
        listOf("Loves heat.", "Stake to support fruit.", "Consistent watering prevents blossom rot.")),
    PlantSpecies("lettuce", "Lettuce", "🥬", PlantSpecies.Difficulty.Beginner, PlantSpecies.Light.Bright, PlantSpecies.Water.Frequent, "vegetable",
        listOf("Cool-season crop.", "Harvest outer leaves.", "Bolts in heat — provide afternoon shade.")),
    PlantSpecies("strawberry", "Strawberry", "🍓", PlantSpecies.Difficulty.Intermediate, PlantSpecies.Light.Direct, PlantSpecies.Water.Frequent, "vegetable",
        listOf("Mulch to keep berries clean.", "Pinch runners for bigger fruit.", "Replace plants every 3 years.")),
    PlantSpecies("rose", "Rose", "🌹", PlantSpecies.Difficulty.Expert, PlantSpecies.Light.Direct, PlantSpecies.Water.Weekly, "flower",
        listOf("Prune in early spring.", "Deep weekly watering.", "Watch for blackspot and aphids.")),
    PlantSpecies("orchid", "Phalaenopsis Orchid", "🌺", PlantSpecies.Difficulty.Intermediate, PlantSpecies.Light.Medium, PlantSpecies.Water.Sparse, "flower",
        listOf("Water by ice cubes once weekly.", "Bright indirect light.", "Reblooms with cool nights.")),
    PlantSpecies("lavender", "Lavender", "💜", PlantSpecies.Difficulty.Intermediate, PlantSpecies.Light.Direct, PlantSpecies.Water.Sparse, "flower",
        listOf("Mediterranean — drainage critical.", "Prune after bloom.", "Pollinators love it.")),
    PlantSpecies("sunflower", "Sunflower", "🌻", PlantSpecies.Difficulty.Beginner, PlantSpecies.Light.Direct, PlantSpecies.Water.Weekly, "flower",
        listOf("Sow directly outdoors.", "Stake tall varieties.", "Faces the sun as it grows.")),
    PlantSpecies("marigold", "Marigold", "🌼", PlantSpecies.Difficulty.Beginner, PlantSpecies.Light.Direct, PlantSpecies.Water.Weekly, "flower",
        listOf("Companion plant — repels pests.", "Easy from seed.", "Deadhead for more blooms.")),
    PlantSpecies("hibiscus", "Hibiscus", "🌺", PlantSpecies.Difficulty.Intermediate, PlantSpecies.Light.Direct, PlantSpecies.Water.Frequent, "flower",
        listOf("Tropical — loves heat and humidity.", "Feed regularly when blooming.", "Bring indoors in cold climates."))
)

fun PlantSpecies.matches(level: String?, interests: Set<String>): Boolean {
    if (level != null && level != "any") {
        val levelMatch = when (level) {
            "beginner" -> difficulty == PlantSpecies.Difficulty.Beginner
            "intermediate" -> difficulty != PlantSpecies.Difficulty.Expert
            "expert" -> true
            else -> true
        }
        if (!levelMatch) return false
    }
    if (interests.isNotEmpty() && category !in interests) return false
    return true
}
