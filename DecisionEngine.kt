package com.mahesh.gdmi.engine

class DecisionEngine {

    fun decide(score: Double): String {
        return when {
            score >= 0.75 -> "STRONG"
            score >= 0.50 -> "YES"
            score >= 0.25 -> "WEAK"
            else -> "NO"
        }
    }
}
