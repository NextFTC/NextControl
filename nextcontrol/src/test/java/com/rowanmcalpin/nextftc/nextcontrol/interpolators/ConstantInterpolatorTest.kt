/*
 * NextFTC: a user-friendly control library for FIRST Tech Challenge
 * Copyright (C) 2025 Rowan McAlpin
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.rowanmcalpin.nextftc.nextcontrol.interpolators

import com.rowanmcalpin.nextftc.nextcontrol.KineticState
import org.junit.Assert.*
import org.junit.Test

class ConstantInterpolatorTest {

    @Test
    fun `constructor parameter is set as goal`() {
        // Arrange
        val expected = KineticState(1.0, 2.0, 3.0)

        // Act
        val interpolator = ConstantInterpolator(expected)

        // Assert
        assertEquals(expected, interpolator.goal)
    }

    @Test
    fun `goal is returned when currentReference is accessed`() {
        // Arrange
        val firstGoal = KineticState(1.0, 2.0, 3.0)
        val secondGoal = KineticState(4.0, 5.0, 6.0)

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