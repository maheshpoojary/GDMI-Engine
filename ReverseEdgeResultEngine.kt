package com.mahesh.gdmi.engine

class ReverseEdgeResultEngine {

    private val calculator = ReverseEdgeCalculator()
    private val decisionEngine = ReverseEdgeDecision()

    fun evaluate(input: ReverseEdgeInput): ReverseEdgeResult {

        val calculated = calculator.calculate(input)

        val decision =
            decisionEngine.decide(calculated.edgeScore)

        val explanation =
            """
            Present = ${input.present}
            Expected = ${input.expected}
            Target = ${input.target}
            Gap = ${calculated.gap}
            Movement = ${input.movement}
            Risk = ${input.risk}
            Timing = ${input.timing}
            Edge Score = ${calculated.edgeScore}
            Decision = $decision
            """.trimIndent()

        return ReverseEdgeResult(
            gap = calculated.gap,
            edgeScore = calculated.edgeScore
        )
    }
}
