package com.mahesh.gdmi.engine

class ReverseEdgeDecision {

    fun decide(edgeScore: Double): String {
        return when {
            edgeScore >= 7.0 -> "STRONG EDGE"
            edgeScore >= 4.0 -> "EDGE"
            edgeScore >= 2.0 -> "WEAK EDGE"
            else -> "NO EDGE"
        }
    }
}
