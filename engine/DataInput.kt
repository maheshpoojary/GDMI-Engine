package com.mahesh.gdmi.engine

class DataInput {

    private val data = mutableMapOf<String, Double>()

    fun add(key: String, value: Double) {
        data[key] = value
    }

    fun get(key: String): Double? {
        return data[key]
    }

    fun getAll(): Map<String, Double> {
        return data.toMap()
    }

    fun remove(key: String) {
        data.remove(key)
    }

    fun clear() {
        data.clear()
    }

    fun isEmpty(): Boolean {
        return data.isEmpty()
    }

    fun size(): Int {
        return data.size
    }
}
