package com.mahesh.gdmi.engine

object ReverseEdgeScreen {

    fun render(input: GDMInput): String {

        val result = GDMIEEngine.calculate(input)

        return buildString {
            appendLine("=== GDMI REVERSE EDGE ===")
            appendLine()
            appendLine("Present: ${input.present}")
            appendLine("Expected: ${input.expected}")
            appendLine("Target: ${input.target}")
            appendLine("Movement: ${input.movement}")
            appendLine("Risk: ${input.risk}")
            appendLine("Timing: ${input.timing}")
            appendLine()
            appendLine("Gap: ${result.gap}")
            appendLine("Target Gap: ${result.targetGap}")
            appendLine("Edge Score: ${result.edgeScore}")
            appendLine("Decision: ${result.decision}")
            appendLine("Confidence: ${result.confidence}%")
            appendLine()
            appendLine(result.explanation)
        }
    }
}
