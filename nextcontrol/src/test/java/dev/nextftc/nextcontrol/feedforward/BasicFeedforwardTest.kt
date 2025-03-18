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
import kotlin.test.Test
import kotlin.test.assertEquals

class BasicFeedforwardTest {

    @Test
    fun `returns zero when all parameters are zero`() {
        // Arrange
        val parameters = BasicFeedforwardParameters(0.0, 0.0, 0.0)
        val feedforward = BasicFeedforward(parameters)
        val reference = KineticState(1.0, 2.0, 3.0)
        val expected = 0.0

        // Act
        val actual = feedforward.calculate(reference)

        // Assert
        assertEquals(expected, actual, 0.0)
    }

    @Test
    fun `calculate returns correct feedforward after constants change`() {
        // Arrange
        val parameters = BasicFeedforwardParameters(1.0, 1.0, 1.0)
        val feedforward = BasicFeedforward(parameters)
        val reference = KineticState(1.0, 1.0, 1.0)

        // Act
        val actual1 = feedforward.calculate(reference)
        parameters.kV = 2.0
        parameters.kA = 2.0
        parameters.kS = 2.0
        val actual2 = feedforward.calculate(reference)

        // Assert
        assertEquals(3.0, actual1, 0.0)
        assertEquals(6.0, actual2, 0.0)
    }

    @Test
    fun `when other gains are zero output is kS times sign of velocity`() {
        // Arrange
        val parameters = BasicFeedforwardParameters(kS = 5.0)
        val feedforward = BasicFeedforward(parameters)
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
    fun `properly applies feedforward`() {
        // Arrange
        val parameters = BasicFeedforwardParameters(1.0, 1.0, 1.0)
        val feedforward = BasicFeedforward(parameters)
        val reference = KineticState(1.0, 2.0, 3.0)

        // Act
        val actual = feedforward.calculate(reference)

        // Assert
        assertEquals(6.0, actual, 0.0)
    }
}