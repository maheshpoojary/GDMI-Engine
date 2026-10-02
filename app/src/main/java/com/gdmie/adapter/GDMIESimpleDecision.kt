package com.gdmie.adapter

/**
 * User-facing decision input.
 *
 * Semantic states are converted to mathematical values only
 * inside the adapter layer. Technical GDMInput values remain
 * hidden from the user-facing model.
 */
data class GDMIESimpleDecision(
    val decisionText: String,
    val currentSituation: CurrentState,
    val goalOutcome: GoalLevel,
    val context: ContextState,
    val momentum: MomentumState,
    val risk: RiskLevel,
    val timing: TimingState,
    val mathResult: Int = 0
)
