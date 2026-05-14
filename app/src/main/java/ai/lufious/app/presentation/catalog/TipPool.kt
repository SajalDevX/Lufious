package ai.lufious.app.presentation.catalog

import java.time.LocalDate

val generalTips: List<String> = listOf(
    "Stick your finger an inch into the soil — if it's dry, water; if not, wait.",
    "Most houseplants prefer to dry out slightly between waterings. Overwatering kills more plants than underwatering.",
    "Rotate your plants 90° each week so they grow evenly toward the light.",
    "Wipe dusty leaves with a damp cloth — clean leaves photosynthesize better.",
    "Empty saucers 15 minutes after watering. Roots sitting in water rot.",
    "South-facing windows give the most light. North-facing the least.",
    "Yellow leaves often mean too much water. Crispy brown edges often mean too little humidity.",
    "Group humidity-loving plants together — they create their own microclimate.",
    "Fertilize during spring and summer growth. Skip in winter.",
    "Repot when roots circle the pot bottom or poke through drainage holes.",
    "Always use pots with drainage holes. Without them, water sits and rots roots.",
    "Pinch the tops of bushy herbs like basil to encourage fuller growth.",
    "Soft tap water often has chlorine — let it sit overnight before using.",
    "Mist tropical plants in dry climates, but skip succulents and cacti.",
    "Move plants away from heating vents and AC units — both dry leaves out fast.",
    "Bottom-watering once a month flushes out salt buildup from the soil.",
    "If light is low, plants stretch toward the window. Move closer or add a grow light.",
    "Cut yellowing leaves at the base — they won't recover, and removing them helps the plant focus energy elsewhere.",
    "Quarantine new plants for 2 weeks to avoid spreading pests to your collection.",
    "A pebble tray under tropical plants adds humidity without overwatering.",
    "Most succulents want to be soaked then bone-dry — not lightly misted.",
    "Snake plants and ZZ plants survive low light and forgetfulness. Great starters.",
    "Use room-temperature water. Cold water shocks roots.",
    "Brown crispy tips often mean too dry air. Bump up humidity.",
    "If leaves wilt suddenly, check the pot — root rot from overwatering looks similar to underwatering.",
    "Cacti need full sun and a long winter rest with almost no water.",
    "Orchids prefer a weekly soak and then nearly dry. Don't keep them constantly wet.",
    "Trim leggy stems back to the soil line to encourage new bushy growth.",
    "If new growth comes in pale or small, your plant likely needs more light.",
    "Propagate pothos by cutting below a leaf node and placing the stem in water.",
    "Add perlite to potting mix for better drainage — especially for succulents.",
    "Don't fertilize a sick plant. Fix the issue first, then feed.",
    "Drafty windows in winter can chill tropical plants. Move them indoors.",
    "Track watering on a schedule — most adults forget which plant they watered when.",
    "Tomatoes need at least 6 hours of direct sun a day to fruit.",
    "Pinch flowers off new herbs to keep the leaves tasty.",
    "If pests show up, isolate the plant and wipe leaves with diluted neem oil.",
    "Spider mites thrive in dry air. Mist regularly to prevent them.",
    "Plants slow down in winter — water less and skip fertilizer.",
    "Talk to your plants. Not science — but checking on them daily catches problems early."
)

fun tipOfTheDay(today: LocalDate = LocalDate.now()): String {
    val idx = (today.toEpochDay().toInt() % generalTips.size + generalTips.size) % generalTips.size
    return generalTips[idx]
}
