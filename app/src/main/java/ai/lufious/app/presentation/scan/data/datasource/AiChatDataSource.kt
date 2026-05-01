package ai.lufious.app.presentation.scan.data.datasource

import ai.lufious.app.presentation.scan.data.models.AiChatMessageModel
import jakarta.inject.Inject
import kotlinx.coroutines.delay

class AiChatDataSource @Inject constructor() {

    @Suppress("UNUSED_PARAMETER")
    suspend fun sendMessage(
        speciesName: String,
        healthStatus: String,
        diagnosis: String,
        userMessage: String
    ): AiChatMessageModel {
        delay(1200L)
        val q = userMessage.lowercase()
        val reply = when {
            "water" in q || "watering" in q || "irrigat" in q ->
                "For $speciesName, water when the top inch of soil is dry to the touch. ${
                    if (healthStatus == "warning")
                        "Given your plant's current stress signs, overwatering is a common culprit — let the soil dry out more between waterings."
                    else
                        "Typically every 7–14 days depending on light levels and season."
                }"
            "light" in q || "sun" in q || "bright" in q ->
                "$speciesName generally thrives in bright indirect light. Avoid harsh afternoon direct sun which can scorch the leaves. A few hours of gentle morning sun is ideal."
            "fertiliz" in q || "feed" in q || "nutrient" in q ->
                "Feed $speciesName with a balanced liquid fertilizer (e.g. 10-10-10 NPK) every 4–6 weeks during the growing season (spring and summer). Hold off on fertilizing in autumn and winter when growth slows."
            "cat" in q || "dog" in q || "pet" in q || "toxic" in q || "safe" in q ->
                "For accurate toxicity information about $speciesName, please check the ASPCA's official toxic plant database or consult your vet. Better safe than sorry with curious pets!"
            "repot" in q || "pot" in q || "roots" in q ->
                "Repot $speciesName every 1–2 years, or when you see roots circling the bottom or escaping the drainage holes. Spring is the best time — go up one pot size and use fresh well-draining potting mix."
            "yellow" in q || "yellowing" in q ->
                "Yellowing leaves on $speciesName usually indicate one of: overwatering (most common), underwatering, low light, or nutrient deficiency. ${
                    if ("overwater" in diagnosis.lowercase())
                        "The diagnosis suggests overwatering — let the soil dry more completely between waterings and ensure good drainage."
                    else
                        "Check soil moisture first, then consider light levels and when you last fertilized."
                }"
            "diagnos" in q || "problem" in q || "issue" in q || "wrong" in q ->
                "Current diagnosis for your $speciesName: $diagnosis"
            "humid" in q || "mist" in q || "spray" in q ->
                "$speciesName appreciates moderate to high humidity. You can mist the leaves, place a pebble tray with water nearby, or group it with other plants to naturally boost humidity — especially important in winter when indoor air gets dry."
            "temperature" in q || "cold" in q || "heat" in q || "warm" in q ->
                "$speciesName prefers temperatures between 18–27°C (65–80°F). Keep it away from cold drafts, air conditioning vents, and heating radiators. Sudden temperature swings stress the plant."
            "propagat" in q || "cutting" in q || "grow more" in q ->
                "$speciesName can typically be propagated through stem cuttings. Take a 10–15 cm cutting just below a node, remove lower leaves, and root it in water or moist soil in a bright indirect spot. Roots usually develop in 2–6 weeks."
            "soil" in q || "mix" in q || "compost" in q ->
                "$speciesName does best in well-draining potting mix. A blend of regular potting soil with perlite (1:1 ratio) works well for most tropical houseplants, preventing waterlogging while retaining some moisture."
            "pest" in q || "bug" in q || "insect" in q || "mite" in q ->
                "Common pests for $speciesName include spider mites, fungus gnats, and mealybugs. Inspect the undersides of leaves regularly. Treat with insecticidal soap spray or neem oil, repeating every 7 days for 3 weeks to break the life cycle."
            else ->
                "I'm your AI plant assistant for your $speciesName. You can ask me about watering schedules, light requirements, fertilizing, humidity, repotting, propagation, pests, or anything else about plant care. What would you like to know?"
        }
        return AiChatMessageModel(
            role = "assistant",
            content = reply,
            timestamp = System.currentTimeMillis()
        )
    }
}
