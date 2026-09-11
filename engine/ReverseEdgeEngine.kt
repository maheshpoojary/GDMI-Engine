package com.mahesh.gdmi.engine

data class MatchState(
    val score: Int,
    val overs: Double,
    val wickets: Int,
    val marketLine: Double,
    val recentOverRuns: Double,
    val oddsOver: Double,
    val oddsUnder: Double
)

data class ReverseEdgeResult(
    val expectedOverRuns: Double,
    val edge: Double,
    val signal: String,
    val confidence: Int
)

object ReverseEdgeEngine {

    fun analyze(state: MatchState): ReverseEdgeResult {

        val balls = oversToBalls(state.overs)
        val completedOvers = balls / 6.0

        val crr = if (completedOvers > 0) {
            state.score / completedOvers
        } else {
            0.0
        }

        // Recent momentum gets more weight than overall CRR.
        val momentumFactor =
            (state.recentOverRuns - crr) * 0.35

        // Wicket pressure.
        val wicketFactor =
            when {
                state.wickets >= 7 -> -1.5
                state.wickets >= 5 -> -0.8
                state.wickets <= 2 -> 0.5
                else -> 0.0
            }

        val expectedRuns =
            (crr + momentumFactor + wicketFactor)
                .coerceIn(1.0, 30.0)

        val edge = expectedRuns - state.marketLine

        val signal = when {
            edge >= 2.0 -> "OVER"
            edge <= -2.0 -> "UNDER"
            else -> "WAIT"
        }

        val confidence = when {
            kotlin.math.abs(edge) >= 3.5 -> 90
            kotlin.math.abs(edge) >= 2.5 -> 80
            kotlin.math.abs(edge) >= 2.0 -> 70
            kotlin.math.abs(edge) >= 1.0 -> 55
            else -> 40
        }

        return ReverseEdgeResult(
            expectedOverRuns = expectedRuns,
            edge = edge,
            signal = signal,
            confidence = confidence
        )
    }

    private fun oversToBalls(overs: Double): Int {
        val wholeOvers = overs.toInt()
        val balls = ((overs - wholeOvers) * 10).toInt()
        return wholeOvers * 6 + balls
    }
}
