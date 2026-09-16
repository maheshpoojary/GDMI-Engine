package com.gdmie

import kotlin.math.abs

object GDMEngine {

    fun calculate(input: GDMInput): GDMResult {

        /*
         * GDMIE GENERAL REVERSE ENGINE v1.0
         *
         * PRESENT
         *    ↓
         * EXPECTED
         *    ↓
         * TARGET
         *    ↓
         * MOMENTUM
         *    ↓
         * REFERENCE GAP
         *    ↓
         * MOVEMENT
         *    ↓
         * RISK + TIMING
         *    ↓
         * REVERSE EDGE
         *    ↓
         * CONFIDENCE
         *    ↓
         * GENERAL DECISION
         *
         * Domain-neutral decision intelligence.
         */

        // ---------------------------------------------------------
        // 1. PRESENT → EXPECTED CORE
        // ---------------------------------------------------------

        val coreExpected =
            (input.presentValue * 0.35) +
            (input.expectedValue * 0.30) +
            (input.targetValue * 0.15) +
            (input.recentMomentum * 0.10) +
            (input.immediateMomentum * 0.10)

        // ---------------------------------------------------------
        // 2. REFERENCE GAP
        // ---------------------------------------------------------

        val referenceGap =
            coreExpected - input.exactMarketLine

        // ---------------------------------------------------------
        // 3. CONTEXT + MOVEMENT + RISK + TIMING
        // ---------------------------------------------------------

        val contextAdjustment =
            (input.twoMinMarketAdvantage * 0.50) +
            (input.oddsMovement * 0.25) +
            (input.timingFactor * 0.15) -
            (input.riskFactor * 0.40)

        // ---------------------------------------------------------
        // 4. FINAL EXPECTED VALUE
        // ---------------------------------------------------------

        val finalExpected =
            coreExpected + contextAdjustment

        // ---------------------------------------------------------
        // 5. REVERSE EDGE
        // ---------------------------------------------------------

        val reverseEdge =
            finalExpected - input.exactMarketLine

        // ---------------------------------------------------------
        // 6. CONFIDENCE
        // ---------------------------------------------------------

        val confidence = when {
            abs(reverseEdge) >= 20.0 -> 0.90
            abs(reverseEdge) >= 10.0 -> 0.75
            abs(reverseEdge) >= 5.0 -> 0.60
            else -> 0.50
        }

        // ---------------------------------------------------------
        // 7. GENERAL DECISION
        // ---------------------------------------------------------

        val decision = when {
            reverseEdge >= 5.0 -> "POSITIVE EDGE"
            reverseEdge <= -5.0 -> "NEGATIVE EDGE"
            else -> "NEUTRAL"
        }

        // ---------------------------------------------------------
        // 8. MOMENTUM
        // ---------------------------------------------------------

        val momentum =
            input.recentMomentum + input.immediateMomentum

        // ---------------------------------------------------------
        // 9. EXPLANATION ENGINE
        // ---------------------------------------------------------

        val explanation = buildString {

            append("GDMIE REVERSE ENGINE\n\n")

            append("PRESENT → EXPECTED → TARGET\n")
            append("CONTEXT → GAP → MOVEMENT\n")
            append("RISK → TIMING → REVERSE EDGE\n\n")

            append("CORE EXPECTATION\n")
            append("--------------------------------\n")
            append(
                "Present × 35%% = %.2f\n"
                    .format(input.presentValue * 0.35)
            )
            append(
                "Expected × 30%% = %.2f\n"
                    .format(input.expectedValue * 0.30)
            )
            append(
                "Target × 15%% = %.2f\n"
                    .format(input.targetValue * 0.15)
            )
            append(
                "Recent Momentum × 10%% = %.2f\n"
                    .format(input.recentMomentum * 0.10)
            )
            append(
                "Immediate Momentum × 10%% = %.2f\n"
                    .format(input.immediateMomentum * 0.10)
            )

            append(
                "Core Expected = %.2f\n\n"
                    .format(coreExpected)
            )

            append("REFERENCE GAP\n")
            append("--------------------------------\n")
            append(
                "Reference Value = %.2f\n"
                    .format(input.exactMarketLine)
            )
            append(
                "Reference Gap = %.2f\n\n"
                    .format(referenceGap)
            )

            append("CONTEXT & MOVEMENT\n")
            append("--------------------------------\n")
            append(
                "2-Min Advantage × 50%% = %.2f\n"
                    .format(input.twoMinMarketAdvantage * 0.50)
            )
            append(
                "Movement × 25%% = %.2f\n"
                    .format(input.oddsMovement * 0.25)
            )
            append(
                "Timing × 15%% = %.2f\n"
                    .format(input.timingFactor * 0.15)
            )
            append(
                "Risk × -40%% = %.2f\n"
                    .format(input.riskFactor * -0.40)
            )

            append(
                "Context Adjustment = %.2f\n\n"
                    .format(contextAdjustment)
            )

            append("FINAL REVERSE CALCULATION\n")
            append("--------------------------------\n")
            append(
                "Final Expected Value = %.2f\n"
                    .format(finalExpected)
            )
            append(
                "Reference Value = %.2f\n"
                    .format(input.exactMarketLine)
            )
            append(
                "Reverse Edge = %.2f\n"
                    .format(reverseEdge)
            )
            append(
                "Momentum = %.2f\n"
                    .format(momentum)
            )
            append(
                "Risk = %.2f\n"
                    .format(input.riskFactor)
            )
            append(
                "Confidence = %.0f%%\n"
                    .format(confidence * 100)
            )
            append(
                "Decision = %s\n\n"
                    .format(decision)
            )

            append("DECISION LOGIC\n")
            append("--------------------------------\n")

            when {
                reverseEdge >= 5.0 -> {
                    append(
                        "Positive Reverse Edge detected.\n"
                    )
                    append(
                        "Final Expected Value is above the Reference Value.\n"
                    )
                }

                reverseEdge <= -5.0 -> {
                    append(
                        "Negative Reverse Edge detected.\n"
                    )
                    append(
                        "Final Expected Value is below the Reference Value.\n"
                    )
                }

                else -> {
                    append(
                        "Reverse Edge is inside the neutral zone.\n"
                    )
                    append(
                        "Current evidence is not strong enough for a directional decision.\n"
                    )
                }
            }

            append("\n")
            append("GDMIE PRINCIPLE\n")
            append(
                "Present data is compared with Expected, Target and Reference values.\n"
            )
            append(
                "Momentum, Movement, Risk and Timing modify the decision context.\n"
            )
            append(
                "The final Reverse Edge determines the strength and direction of the decision."
            )
        }

        // ---------------------------------------------------------
        // 10. RESULT
        // ---------------------------------------------------------

        return GDMResult(
            presentValue = input.presentValue,
            expectedValue = finalExpected,
            marketValue = input.exactMarketLine,
            gap = referenceGap,
            momentum = momentum,
            risk = input.riskFactor,
            edge = reverseEdge,
            confidence = confidence,
            decision = decision,
            explanation = explanation
        )
    }
}


