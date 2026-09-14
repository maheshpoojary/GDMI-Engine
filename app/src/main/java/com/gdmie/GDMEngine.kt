package com.gdmie

import kotlin.math.abs

object GDMEngine {

    fun calculate(input: GDMInput): GDMResult {

        // Core match estimate
        val coreExpected =
            (input.presentValue * 0.35) +
            (input.expectedValue * 0.30) +
            (input.targetValue * 0.15) +
            (input.recentMomentum * 0.10) +
            (input.immediateMomentum * 0.10)

        // Market gap
        val marketGap =
            coreExpected - input.exactMarketLine

        // Market / momentum adjustment
        val marketAdjustment =
            (input.twoMinMarketAdvantage * 0.50) +
            (input.oddsMovement * 0.25) +
            (input.timingFactor * 0.15) -
            (input.riskFactor * 0.40)

        val expected =
            coreExpected + marketAdjustment

        val edge =
            expected - input.exactMarketLine

        val confidence = when {
            abs(edge) >= 20.0 -> 0.90
            abs(edge) >= 10.0 -> 0.75
            abs(edge) >= 5.0 -> 0.60
            else -> 0.50
        }

        val decision = when {
            edge >= 5.0 -> "OVER"
            edge <= -5.0 -> "UNDER"
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
