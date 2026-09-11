package com.mahesh.gdmi.engine

data class GDMIResult(
    val score: Double,
    val decision: String,
    val explanation: String
)

class GDMIECore {

    fun process(input: Map<String, Double>): GDMIResult {

        if (input.isEmpty()) {
            return GDMIResult(
                score = 0.0,
                decision = "NO_DATA",
                explanation = "No input data was provided."
            )
        }

        val score = input.values.average()

        val decision = when {
            score >= 80.0 -> "STRONG"
            score >= 60.0 -> "POSITIVE"
            score >= 40.0 -> "NEUTRAL"
            score >= 20.0 -> "NEGATIVE"
            else -> "WEAK"
        }

        val explanation =
            "GDMI Core processed ${input.size} input values. " +
            "Calculated score: ${"%.2f".format(score)}. " +
            "Decision level: $decision."

        return GDMIResult(
            score = score,
            decision = decision,
            explanation = explanation
        )
    }
}
