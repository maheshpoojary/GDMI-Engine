package com.mahesh.gdmi.engine

class ReverseEdgeViewModel {

    private val reverseEdgeEngine =
        ReverseEdgeResultEngine()

    private val calculationHistory =
        CalculationHistory()

    fun analyze(
        input: ReverseEdgeInput
    ): ReverseEdgeResult {

        val result =
            reverseEdgeEngine.evaluate(input)

        calculationHistory.add(
            CalculationRecord(
                score = result.edgeScore,
                decision = result.decision,
                explanation = result.explanation
            )
        )

        return result
    }

    fun getHistory(): List<CalculationRecord> {
        return calculationHistory.getAll()
    }

    fun clearHistory() {
        calculationHistory.clear()
    }
}
