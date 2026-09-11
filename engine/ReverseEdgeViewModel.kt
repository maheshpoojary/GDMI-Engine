package com.mahesh.gdmi.engine

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ReverseEdgeViewModel : ViewModel() {

    private val _result = MutableStateFlow<ReverseEdgeResult?>(null)
    val result: StateFlow<ReverseEdgeResult?> = _result.asStateFlow()

    fun analyze(
        score: Int,
        overs: Double,
        wickets: Int,
        marketLine: Double,
        recentOverRuns: Double,
        oddsOver: Double,
        oddsUnder: Double
    ) {
        val state = MatchState(
            score = score,
            overs = overs,
            wickets = wickets,
            marketLine = marketLine,
            recentOverRuns = recentOverRuns,
            oddsOver = oddsOver,
            oddsUnder = oddsUnder
        )

        _result.value = ReverseEdgeEngine.analyze(state)
    }
}
