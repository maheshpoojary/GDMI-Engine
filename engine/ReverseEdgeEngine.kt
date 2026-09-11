package com.mahesh.gdmi.engine

import kotlin.math.abs

object ReverseEdgeEngine {

    fun calculate(input: GDMInput): GDMResult {

        val gap = input.expected - input.present
        val targetGap = input.target - input.present

        val gapScore = abs(gap)
        val movementEffect = input.movement
        val riskEffect = input.risk
        val timingEffect = input.timing

        val edgeScore = (
            gapScore +
            movementEffect -
            riskEffect +
            timingEffect
        ).coerceAtLeast(0.0)

        val decision = when {
            edgeScore >= 70.0 -> "STRONG EDGE"
            edgeScore >= 50.0 -> "EDGE"
            edgeScore >= 30.0 -> "WEAK EDGE"
            else -> "NO CLEAR EDGE"
        }

        val explanation = buildString {
            append("Present = ${input.present}. ")
            append("Expected = ${input.expected}. ")
            append("Target = ${input.target}. ")
            append("Gap = $gap. ")
            append("Target Gap = $targetGap. ")
            append("Movement = ${input.movement}. ")
            append("Risk = ${input.risk}. ")
            append("Timing = ${input.timing}. ")
            append("Final Edge Score = ${"%.2f".format(edgeScore)}. ")
            append("Decision = $decision.")
        }

        return GDMResult(
            gap = gap,
            edgeScore = edgeScore,
            decision = decision,
            explanation = explanation
        )
    }
}
