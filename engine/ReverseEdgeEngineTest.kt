package engine

import kotlin.test.Test
import kotlin.test.assertEquals

class ReverseEdgeEngineTest {

    @Test
    fun strongMomentumShouldGiveOver() {
        val state = MatchState(
            score = 120,
            balls = 60,
            wickets = 2,
            recentOverRuns = 15,
            marketLine = 11.0,
            overOdds = 1.90,
            underOdds = 1.90
        )

        val result = ReverseEdgeEngine.analyze(state)

        assertEquals(13.55, result.expectedOverRuns, 0.01)
        assertEquals(2.55, result.edge, 0.01)
        assertEquals("OVER", result.decision)
    }

    @Test
    fun weakMomentumShouldGiveUnder() {
        val state = MatchState(
            score = 80,
            balls = 60,
            wickets = 8,
            recentOverRuns = 5,
            marketLine = 8.0,
            overOdds = 1.90,
            underOdds = 1.90
        )

        val result = ReverseEdgeEngine.analyze(state)

        assertEquals(5.95, result.expectedOverRuns, 0.01)
        assertEquals(-2.05, result.edge, 0.01)
        assertEquals("UNDER", result.decision)
    }

    @Test
    fun balancedStateShouldGiveNoBet() {
        val state = MatchState(
            score = 90,
            balls = 60,
            wickets = 4,
            recentOverRuns = 9,
            marketLine = 9.0,
            overOdds = 1.90,
            underOdds = 1.90
        )

        val result = ReverseEdgeEngine.analyze(state)

        assertEquals(9.0, result.expectedOverRuns, 0.01)
        assertEquals(0.0, result.edge, 0.01)
        assertEquals("NO BET", result.decision)
    }

    @Test
    fun lowWicketsShouldIncreaseExpectedRuns() {
        val state = MatchState(
            score = 100,
            balls = 60,
            wickets = 2,
            recentOverRuns = 10,
            marketLine = 10.0,
            overOdds = 1.90,
            underOdds = 1.90
        )

        val result = ReverseEdgeEngine.analyze(state)

        assertEquals(10.5, result.expectedOverRuns, 0.01)
    }

    @Test
    fun manyWicketsShouldReduceExpectedRuns() {
        val state = MatchState(
            score = 100,
            balls = 60,
            wickets = 8,
            recentOverRuns = 10,
            marketLine = 10.0,
            overOdds = 1.90,
            underOdds = 1.90
        )

        val result = ReverseEdgeEngine.analyze(state)

        assertEquals(9.0, result.expectedOverRuns, 0.01)
    }
}
