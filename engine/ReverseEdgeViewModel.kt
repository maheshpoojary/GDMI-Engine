package engine

data class ReverseEdgeUiState(
    val score: Int = 0,
    val balls: Int = 0,
    val wickets: Int = 0,
    val recentOverRuns: Int = 0,
    val marketLine: Double = 0.0,
    val overOdds: Double = 0.0,
    val underOdds: Double = 0.0,
    val expectedOverRuns: Double = 0.0,
    val edge: Double = 0.0,
    val decision: String = "NO BET"
)

class ReverseEdgeViewModel {

    var uiState = ReverseEdgeUiState()
        private set

    fun analyze(
        score: Int,
        balls: Int,
        wickets: Int,
        recentOverRuns: Int,
        marketLine: Double,
        overOdds: Double,
        underOdds: Double
    ) {
        val state = MatchState(
            score = score,
            balls = balls,
            wickets = wickets,
            recentOverRuns = recentOverRuns,
            marketLine = marketLine,
            overOdds = overOdds,
            underOdds = underOdds
        )

        val result = ReverseEdgeEngine.analyze(state)

        uiState = ReverseEdgeUiState(
            score = score,
            balls = balls,
            wickets = wickets,
            recentOverRuns = recentOverRuns,
            marketLine = marketLine,
            overOdds = overOdds,
            underOdds = underOdds,
            expectedOverRuns = result.expectedOverRuns,
            edge = result.edge,
            decision = result.decision
        )
    }
}
