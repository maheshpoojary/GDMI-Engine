package com.mahesh.gdmi.engine

data class GDMInput(
    val present: Double,
    val expected: Double,
    val target: Double,
    val movement: Double,
    val risk: Double,
    val timing: Double
)

data class GDMResult(
    val gap: Double,
    val targetGap: Double,
    val edgeScore: Double,
    val decision: String,
    val confidence: Int,
    val explanation: String
)
