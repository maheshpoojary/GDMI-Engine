package com.gdmie.network.model

data class GDMIEResponse(
    val presentValue: Double,
    val expectedValue: Double,
    val marketValue: Double,
    val gap: Double,
    val momentum: Double,
    val risk: Double,
    val edge: Double,
    val confidence: Double,
    val decision: String,
    val explanation: String
)
