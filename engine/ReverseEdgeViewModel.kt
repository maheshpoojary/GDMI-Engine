package com.mahesh.gdmi.engine

class ReverseEdgeViewModel {

    fun analyze(input: Map<String, Double>): GDMIResult {
        return GDMIEngine().process(input)
    }
}
