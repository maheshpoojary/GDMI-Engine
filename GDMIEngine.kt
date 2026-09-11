package com.mahesh.gdmi.engine

data class GDMIResult(
    val score: Double,
    val decision: String,
    val explanation: String
)

class GDMIEngine {

    private val calculation = AutomaticCalculation()
    private val decisionEngine = DecisionEngine()
    private val explanationEngine = ExplanationEngine()

    fun process(input: Map<String, Double>): GDMIResult {

        if (input.isEmpty()) {
            return GDMIResult(
                score = 0.0,
                decision = "NO_DATA",
                explanation = "No input data available."
            )
        }

        val score = calculation.calculate(input)

        val decision = decisionEngine.decide(score)

        val explanation =
            explanationEngine.explain(score, decision)

        return GDMIResult(
            score = score,
            decision = decision,
            explanation = explanation
        )
    }
}
