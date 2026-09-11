package engine

data class MatchState(
    val score: Int,
    val balls: Int,
    val wickets: Int,
    val recentOverRuns: Int,
    val marketLine: Double,
    val overOdds: Double,
    val underOdds: Double
)

data class ReverseEdgeResult(
    val expectedOverRuns: Double,
    val edge: Double,
    val decision: String
)

object ReverseEdgeEngine {

    fun analyze(state: MatchState): ReverseEdgeResult {

        val overs = state.balls / 6.0

        val crr =
            if (overs > 0)
                state.score / overs
            else
                0.0

        val recentMomentum =
            state.recentOverRuns - crr

        val wicketFactor =
            when {
                state.wickets >= 8 -> -2.0
                state.wickets >= 6 -> -1.0
                state.wickets <= 2 -> 1.0
                else -> 0.0
            }

        val oddsSignal =
            ((1.0 / state.overOdds) -
             (1.0 / state.underOdds)) * 10.0

        val expectedOverRuns =
            (
                crr +
                recentMomentum * 0.35 +
                wicketFactor * 0.5 +
                oddsSignal * 0.25
            ).coerceIn(2.0, 20.0)

        val edge =
            expectedOverRuns - state.marketLine

        val decision =
            when {
                edge >= 1.5 -> "OVER"
                edge <= -1.5 -> "UNDER"
                else -> "NO BET"
            }

        return ReverseEdgeResult(
            expectedOverRuns = expectedOverRuns,
            edge = edge,
            decision = decision
        )
    }
}
