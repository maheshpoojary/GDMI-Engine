package com.mahesh.gdmi.engine

import kotlin.test.Test
import kotlin.test.assertEquals

class GDMIEEngineTest {

    @Test
    fun strongEdgeShouldBeDetected() {
        val input = GDMInput(
            present = 10.0,
            expected = 15.0,
            target = 20.0,
            movement = 2.0,
            risk = 1.0,
            timing = 1.0
        )

        val result = GDMIEEngine.calculate(input)

        assertEquals(5.0, result.gap)
        assertEquals(10.0, result.targetGap)
        assertEquals(7.0, result.edgeScore)
        assertEquals("STRONG EDGE", result.decision)
        assertEquals(90, result.confidence)
    }

    @Test
    fun normalEdgeShouldBeDetected() {
        val input = GDMInput(
            present = 10.0,
            expected = 14.0,
            target = 18.0,
            movement = 1.0,
            risk = 1.0,
            timing = 0.0
        )

        val result = GDMIEEngine.calculate(input)

        assertEquals(4.0, result.edgeScore)
        assertEquals("EDGE", result.decision)
        assertEquals(75, result.confidence)
    }

    @Test
    fun weakEdgeShouldBeDetected() {
        val input = GDMInput(
            present = 10.0,
            expected = 12.0,
            target = 15.0,
            movement = 1.0,
            risk = 1.0,
            timing = 0.0
        )

        val result = GDMIEEngine.calculate(input)

        assertEquals(2.0, result.edgeScore)
        assertEquals("WEAK EDGE", result.decision)
        assertEquals(60, result.confidence)
    }

    @Test
    fun noEdgeShouldBeDetected() {
        val input = GDMInput(
            present = 10.0,
            expected = 11.0,
            target = 12.0,
            movement = 0.5,
            risk = 1.0,
            timing = 0.0
        )

        val result = GDMIEEngine.calculate(input)

        assertEquals(0.5, result.edgeScore)
        assertEquals("NO EDGE", result.decision)
        assertEquals(40, result.confidence)
    }

    @Test
    fun negativeGapShouldUseAbsoluteValue() {
        val input = GDMInput(
            present = 15.0,
            expected = 10.0,
            target = 20.0,
            movement = 0.0,
            risk = 0.0,
            timing = 0.0
        )

        val result = GDMIEEngine.calculate(input)

        assertEquals(-5.0, result.gap)
        assertEquals(5.0, result.edgeScore)
        assertEquals("EDGE", result.decision)
        assertEquals(75, result.confidence)
    }
}
