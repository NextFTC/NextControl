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

package dev.nextftc.nextcontrol.feedforward

import dev.nextftc.nextcontrol.utils.KineticState
import kotlin.test.assertEquals
import kotlin.test.Test

class ElevatorFeedforwardTest {
    @Test
    fun `calculate returns 0 feedforward when parameters are 0`() {
        // Arrange
        val parameters = GravityFeedforwardParameters() // Default to 0
        val feedforward = ElevatorFeedforward(parameters)
        val reference = KineticState(1.0, 2.0, 3.0)

        // Act
        val actual = feedforward.calculate(reference)

        // Assert
        assertEquals(0.0, actual, 0.0)
    }

    @Test
    fun `calculate returns correct feedforward`() {
        // Arrange
       val parameters = GravityFeedforwardParameters(1.0, 1.0, 1.0, 1.0)
        val feedforward = ElevatorFeedforward(parameters)
        val reference = KineticState(1.0, 2.0, 3.0)

        // Act
        val actual = feedforward.calculate(reference)

        // Assert
        assertEquals(7.0, actual, 0.0)
    }

    @Test
    fun `calculate returns correct feedforward after constants change`() {
        // Arrange
        val parameters = GravityFeedforwardParameters(1.0, 1.0, 1.0, 1.0)
        val feedforward = ElevatorFeedforward(parameters)
        val reference = KineticState(1.0, 1.0, 1.0)

        // Act
        val actual1 = feedforward.calculate(reference)
        parameters.kV = 2.0
        parameters.kA = 2.0
        parameters.kS = 2.0
        parameters.kG = 2.0
        val actual2 = feedforward.calculate(reference)

        // Assert
        assertEquals(4.0, actual1, 0.0)
        assertEquals(8.0, actual2, 0.0)
    }

    @Test
    fun `kS works correctly`() {
        // Arrange
        val parameters = GravityFeedforwardParameters(kS = 1.0)
        val feedforward = ElevatorFeedforward(parameters)
        val reference = KineticState(0.0, -1.0, 0.0)

        // Act
        val actual = feedforward.calculate(reference)

        // Assert
        assertEquals(-1.0, actual, 0.0)
    }

    @Test
    fun `kG works correctly`() {
        // Arrange
        val parameters = GravityFeedforwardParameters(kG = 1.0)
        val feedforward = ElevatorFeedforward(parameters)
        val reference = KineticState(0.0, -1.0, 0.0)

        // Act
        val actual = feedforward.calculate(reference)

        // Assert
        assertEquals(1.0, actual, 0.0)
    }
}