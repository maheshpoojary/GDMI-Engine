package com.gdmie

object GDMEngine {

    fun calculate(input: GDMInput): GDMResult {

        val expected =
            (input.presentValue * 0.30) +
            (input.recentValue * 0.20) +
            (input.expectedValue * 0.20) +
            (input.marketValue * 0.10) +
            (input.contextFactor * 0.10) +
            (input.momentumFactor * 0.05) +
            (input.riskFactor * 0.05)

        val edge = expected - input.marketValue

        val confidence = when {
            kotlin.math.abs(edge) >= 20 -> 0.90
            kotlin.math.abs(edge) >= 10 -> 0.75
            kotlin.math.abs(edge) >= 5 -> 0.60
            else -> 0.50
        }

        val decision = when {
            edge > 0 -> "POSITIVE"
            edge < 0 -> "NEGATIVE"
            else -> "NEUTRAL"
        }

        return GDMResult(
            expectedValue = expected,
            edge = edge,
            confidence = confidence,
            decision = decision
        )
    }
}
