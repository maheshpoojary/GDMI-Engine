package com.mahesh.gdmi.engine

class ExplanationEngine {

    fun explain(
        score: Double,
        decision: String
    ): String {

        return when (decision) {
            "STRONG" ->
                "Strong decision confidence. Score is ${"%.2f".format(score)}."

            "YES" ->
                "Positive decision signal. Score is ${"%.2f".format(score)}."

            "WEAK" ->
                "Weak decision signal. More data is recommended."

            else ->
                "Insufficient decision confidence."
        }
    }
}
