package com.gdmie.adapter

import com.gdmie.GDMInput

object GDMIESimpleDecisionAdapter {

    fun toGDMInput(decision: GDMIESimpleDecision): GDMInput {

        require(decision.decisionText.isNotBlank()) {
            "Decision cannot be empty"
        }

        val presentValue =
            GDMIENormalization.currentState(decision.currentSituation)

        val targetValue =
            GDMIENormalization.goalLevel(decision.goalOutcome)

        val contextFactor =
            GDMIENormalization.context(decision.context)

        val momentum =
            GDMIENormalization.momentum(decision.momentum)

        val risk =
            GDMIENormalization.risk(decision.risk)

        val timing =
            GDMIENormalization.timing(decision.timing)

        val expectedValue =
            (presentValue + targetValue) / 2.0

        return GDMInput(
            presentValue = presentValue,
            expectedValue = expectedValue,
            targetValue = targetValue,
            recentMomentum = momentum,
            immediateMomentum = momentum,
            twoMinMarketAdvantage = contextFactor,
            exactMarketLine = targetValue,
            oddsMovement = 0.0,
            timingFactor = timing,
            riskFactor = risk
        )
    }
}
