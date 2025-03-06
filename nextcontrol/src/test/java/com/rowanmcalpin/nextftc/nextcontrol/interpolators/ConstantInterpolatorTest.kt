package com.rowanmcalpin.nextftc.nextcontrol.interpolators

import com.rowanmcalpin.nextftc.nextcontrol.KineticState
import com.rowanmcalpin.nextftc.nextcontrol.of
import org.junit.Assert.*
import org.junit.Test

class ConstantInterpolatorTest {

    @Test
    fun `constructor parameter is set as goal`() {
        // Arrange
        val expected = KineticState.of(1.0, 2.0, 3.0)

        // Act
        val interpolator = ConstantInterpolator(expected)

        // Assert
        assertEquals(expected, interpolator.goal)
    }

    @Test
    fun `goal is returned when currentReference is accessed`() {
        // Arrange
        val firstGoal = KineticState.of(1.0, 2.0, 3.0)
        val secondGoal = KineticState.of(4.0, 5.0, 6.0)

        val interpolator = ConstantInterpolator(firstGoal)

        // Act
        val firstReference = interpolator.currentReference

        interpolator.goal = secondGoal

        val secondReference = interpolator.currentReference

        // Assert
        assertEquals(firstGoal, firstReference)
        assertEquals(secondGoal, secondReference)
    }
}