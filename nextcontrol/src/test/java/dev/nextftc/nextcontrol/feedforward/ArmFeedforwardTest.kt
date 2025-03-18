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

import dev.nextftc.nextcontrol.KineticState
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sign
import kotlin.test.Test
import kotlin.test.assertEquals

class ArmFeedforwardTest {

    @Test
    fun `returns zero when all parameters are zero`() {
        // Arrange
        val parameters = GravityFeedforwardParameters(0.0, 0.0, 0.0, 0.0)
        val feedforward = ArmFeedforward(parameters)
        val reference = KineticState(1.0, 2.0, 3.0)
        val expected = 0.0

        // Act
        val actual = feedforward.calculate(reference)

        // Assert
        assertEquals(expected, actual, 0.0)
    }

    @Test
    fun `changing parameters class affects feedforward output`() {
        // Arrange
        val parameters = GravityFeedforwardParameters(1.0, 1.0, 1.0, 1.0)
        val feedforward = ArmFeedforward(parameters)
        val reference = KineticState(3.0, 5.0, 7.0)
        val expected1 = parameters.kG * cos(3.0) + parameters.kV * 5.0 + parameters.kA * 7.0 +
                parameters.kS * 5.0.sign
        val expected2 = 2.0 * cos(3.0) + 2.0 * 5.0 + 2.0 * 7.0 + 2.0 * 5.0.sign

        // Act
        val actual1 = feedforward.calculate(reference)
        parameters.kV = 2.0
        parameters.kA = 2.0
        parameters.kS = 2.0
        parameters.kG = 2.0
        val actual2 = feedforward.calculate(reference)

        // Assert
        assertEquals(expected1, actual1, 1e-6)
        assertEquals(expected2, actual2, 1e-6)
    }

    @Test
    fun `when other gains are zero output is kS times sign of velocity`() {
        // Arrange
        val parameters = GravityFeedforwardParameters(kS = 5.0)
        val feedforward = ArmFeedforward(parameters)
        val reference1 = KineticState(6.0, -2.0, 7.0)
        val reference2 = KineticState(-10.0, 3.0, 12.0)
        val expected1 = 5.0 * -1
        val expected2 = 5.0 * 1

        // Act
        val actual1 = feedforward.calculate(reference1)
        val actual2 = feedforward.calculate(reference2)

        // Assert
        assertEquals(expected1, actual1, 0.0)
        assertEquals(expected2, actual2, 0.0)
    }

    @Test
    fun `when other gains are zero output is kG times cosine of position`() {
        // Arrange
        val parameters = GravityFeedforwardParameters(kG = 2.5)
        val feedforward = ArmFeedforward(parameters)
        val reference1 = KineticState(PI / 4, 7.0, 8.0)
        val reference2 = KineticState(5.0, -6.5, 2.6)
        val expected1 = parameters.kG * cos(PI / 4)
        val expected2 = parameters.kG * cos(5.0)

        // Act
        val actual1 = feedforward.calculate(reference1)
        val actual2 = feedforward.calculate(reference2)

        // Assert
        assertEquals(expected1, actual1, 1e-6)
        assertEquals(expected2, actual2, 1e-6)
    }

    @Test
    fun `properly applies feedforward`() {
        // Arrange
        val parameters = GravityFeedforwardParameters(1.0, 2.0, 3.0, 4.0)
        val feedforwardElement = ArmFeedforward(parameters)
        val reference = KineticState(1.0, 2.0, 3.0)
        val expected = parameters.kG * cos(1.0) + parameters.kV * 2.0 + parameters.kA * 3.0 +
                parameters.kS * 2.0.sign

        // Act
        val actual = feedforwardElement.calculate(reference)

        // Assert
        assertEquals(expected, actual, 1e-6)
    }
}