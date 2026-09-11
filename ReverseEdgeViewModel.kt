package com.mahesh.gdmi.engine

class ReverseEdgeViewModel {

    private val reverseEdgeEngine =
        ReverseEdgeResultEngine()

    fun analyze(
        input: ReverseEdgeInput
    ): ReverseEdgeResult {

        return reverseEdgeEngine.evaluate(input)
    }
}
