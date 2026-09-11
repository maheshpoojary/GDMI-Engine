package com.gdmie

data class GDMInput(
    val presentValue: Double,
    val recentValue: Double,
    val expectedValue: Double,
    val marketValue: Double,
    val contextFactor: Double,
    val momentumFactor: Double,
    val riskFactor: Double
)
