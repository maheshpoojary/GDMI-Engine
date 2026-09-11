package com.mahesh.gdmi.engine

data class ReverseEdgeInput(
    val present: Double,
    val expected: Double,
    val target: Double,
    val movement: Double,
    val risk: Double,
    val timing: Double
)
