package com.mahesh.gdmi.engine

class GDMIECore {

    fun process(input: Map<String, Double>): Double {
        if (input.isEmpty()) {
            return 0.0
        }

        return input.values.average()
    }
}
