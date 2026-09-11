package com.mahesh.gdmi.engine

class ReverseEdgeViewModel {

    fun analyze(input: GDMInput): GDMResult {
        return GDMIEEngine.calculate(input)
    }
}
