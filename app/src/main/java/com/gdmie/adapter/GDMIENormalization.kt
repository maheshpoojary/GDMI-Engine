package com.gdmie.adapter

object GDMIENormalization {

    fun currentState(state: CurrentState): Double =
        when (state) {
            CurrentState.WEAK -> 25.0
            CurrentState.UNCERTAIN -> 40.0
            CurrentState.STABLE -> 60.0
            CurrentState.STRONG -> 75.0
        }

    fun goalLevel(goal: GoalLevel): Double =
        when (goal) {
            GoalLevel.SMALL_IMPROVEMENT -> 40.0
            GoalLevel.MODERATE_IMPROVEMENT -> 60.0
            GoalLevel.MAJOR_IMPROVEMENT -> 75.0
            GoalLevel.LONG_TERM_TRANSFORMATION -> 90.0
        }

    fun context(state: ContextState): Double =
        when (state) {
            ContextState.FAVOURABLE -> 25.0
            ContextState.NORMAL -> 0.0
            ContextState.UNCERTAIN -> -10.0
            ContextState.UNFAVOURABLE -> -25.0
        }

    fun momentum(state: MomentumState): Double =
        when (state) {
            MomentumState.IMPROVING -> 75.0
            MomentumState.STABLE -> 50.0
            MomentumState.DECLINING -> 25.0
        }

    fun risk(level: RiskLevel): Double =
        when (level) {
            RiskLevel.LOW -> 25.0
            RiskLevel.MEDIUM -> 50.0
            RiskLevel.HIGH -> 75.0
        }

    fun timing(state: TimingState): Double =
        when (state) {
            TimingState.NOW -> 75.0
            TimingState.SOON -> 50.0
            TimingState.LATER -> 25.0
        }

    fun numericMomentum(value: Int): Double =
        value.coerceIn(0, 100).toDouble()

    fun numericRisk(value: Int): Double =
        value.coerceIn(0, 100).toDouble()

    fun numericTiming(value: Int): Double =
        value.coerceIn(0, 100).toDouble()
}
