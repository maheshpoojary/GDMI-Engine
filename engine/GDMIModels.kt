package com.mahesh.gdmi.engine

data class GDMIInput(
    val present: Double,
    val expected: Double,
    val target: Double,
    val movement: Double,
    val risk: Double,
    val timing: Double
)

data class GDMIResult(
    val gap: Double,
    val edgeScore: Double,
    val decision: String,
    val explanation: String
)
