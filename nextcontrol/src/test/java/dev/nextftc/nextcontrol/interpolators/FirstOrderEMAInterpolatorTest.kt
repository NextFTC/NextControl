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

package dev.nextftc.nextcontrol.interpolators

import dev.nextftc.nextcontrol.KineticState
import kotlin.test.Test
import kotlin.test.assertEquals

class FirstOrderEMAInterpolatorTest {

    @Test
    fun `smoothly interpolates from zero to five`() {
        // Arrange
        val parameters = FirstOrderEMAParameters(0.5)
        val interpolator = FirstOrderEMAInterpolator(parameters)
        interpolator.goal = KineticState(5.0)

        // Calculate
        val c1 = interpolator.currentReference.position
        val c2 = interpolator.currentReference.position
        val c3 = interpolator.currentReference.position
        val c4 = interpolator.currentReference.position
        val c5 = interpolator.currentReference.position

        // Assert
        assertEquals(2.5, c1, 0.0)
        assertEquals(3.75, c2, 0.0)
        assertEquals(4.375, c3, 0.0)
        assertEquals(4.6875, c4, 0.0)
        assertEquals(4.84375, c5, 0.0)
    }

    @Test
    fun `smoothly interpolates back to zero`() {
        // Arrange
        val parameters = FirstOrderEMAParameters(0.5)
        val interpolator = FirstOrderEMAInterpolator(parameters)
        interpolator.goal = KineticState(5.0)

        // Calculate
        interpolator.currentReference.position
        interpolator.currentReference.position
        interpolator.currentReference.position
        interpolator.currentReference.position
        interpolator.currentReference.position

        interpolator.goal = KineticState(0.0)

        val c1 = interpolator.currentReference.position
        val c2 = interpolator.currentReference.position
        val c3 = interpolator.currentReference.position
        val c4 = interpolator.currentReference.position
        val c5 = interpolator.currentReference.position

        assertEquals(2.421875, c1, 0.0)
        assertEquals(1.2109375, c2, 0.0)
        assertEquals(0.60546875, c3, 0.0)
        assertEquals(0.302734375, c4, 0.0)
        assertEquals(0.1513671875, c5, 0.0)
    }
}