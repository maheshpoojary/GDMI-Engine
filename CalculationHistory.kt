package com.mahesh.gdmi.engine

data class CalculationRecord(
    val score: Double,
    val decision: String,
    val explanation: String
)

class CalculationHistory {

    private val history = mutableListOf<CalculationRecord>()

    fun add(record: CalculationRecord) {
        history.add(record)
    }

    fun getAll(): List<CalculationRecord> {
        return history.toList()
    }

    fun clear() {
        history.clear()
    }
}
