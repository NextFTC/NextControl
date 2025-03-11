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
import org.junit.Assert.*
import org.junit.Test

class NullFeedforwardTest {
    @Test
    fun `calculate returns 0 feedforward`() {
        // Arrange
        val feedforward = NullFeedforward()
        val reference = KineticState(1.0, 2.0, 3.0)

        // Act
        val actual = feedforward.calculate(reference)

        // Assert
        assertEquals(0.0, actual, 0.0)
    }

    @Test
    fun `calculate returns 0 feedforward even for NaN and infinity input`() {
        // Arrange
        val feedforward = NullFeedforward()
        val reference = KineticState(1.0, Double.POSITIVE_INFINITY, Double.NaN)

        // Act
        val actual = feedforward.calculate(reference)

        // Assert
        assertEquals(0.0, actual, 0.0)
    }
}