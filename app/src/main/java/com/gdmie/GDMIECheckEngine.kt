package com.gdmie

import kotlin.math.abs

object GDMIECheckEngine {

    data class CheckResult(
        val expectedValue: Double,
        val actualValue: Double,
        val error: Double,
        val absoluteError: Double,
        val decisionQuality: String
    )

    fun check(
        expectedValue: Double,
        actualValue: Double
    ): CheckResult {

        val error = actualValue - expectedValue
        val absoluteError = abs(error)

        val decisionQuality = when {
            absoluteError <= 2.0 -> "VERY CLOSE"
            absoluteError <= 5.0 -> "CLOSE"
            absoluteError <= 10.0 -> "MODERATE GAP"
            else -> "LARGE GAP"
        }

        return CheckResult(
            expectedValue = expectedValue,
            actualValue = actualValue,
            error = error,
            absoluteError = absoluteError,
            decisionQuality = decisionQuality
        )
    }
}
