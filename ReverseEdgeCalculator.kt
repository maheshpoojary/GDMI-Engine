package com.mahesh.gdmi.engine

import kotlin.math.abs

data class ReverseEdgeResult(
    val gap: Double,
    val edgeScore: Double
)

class ReverseEdgeCalculator {

    fun calculate(input: ReverseEdgeInput): ReverseEdgeResult {

        val gap = input.expected - input.target

        val edgeScore =
            abs(gap) +
            input.movement +
            input.timing -
            input.risk

        return ReverseEdgeResult(
            gap = gap,
            edgeScore = edgeScore
        )
    }
}
