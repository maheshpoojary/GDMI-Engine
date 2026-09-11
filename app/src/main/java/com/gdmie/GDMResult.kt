package com.gdmie

data class GDMResult(
    val expectedValue: Double,
    val edge: Double,
    val confidence: Double,
    val decision: String
)
