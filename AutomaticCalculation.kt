package com.mahesh.gdmi.engine

class AutomaticCalculation {

    fun calculate(input: Map<String, Double>): Double {

        if (input.isEmpty()) {
            return 0.0
        }

        return input.values.average()
    }

    fun calculateSum(input: Map<String, Double>): Double {
        return input.values.sum()
    }

    fun calculateCount(input: Map<String, Double>): Int {
        return input.size
    }
}
