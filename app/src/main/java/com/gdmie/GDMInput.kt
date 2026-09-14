package com.gdmie

data class GDMInput(
    val presentValue: Double,
    val expectedValue: Double,
    val targetValue: Double,
    val recentMomentum: Double,
    val immediateMomentum: Double,
    val twoMinMarketAdvantage: Double,
    val exactMarketLine: Double,
    val odds: Double,
    val oddsMovement: Double,
    val timingFactor: Double,
    val riskFactor: Double
)
