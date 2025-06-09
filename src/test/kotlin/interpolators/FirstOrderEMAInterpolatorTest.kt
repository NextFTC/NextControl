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

import dev.nextftc.control.KineticState
import dev.nextftc.control.interpolators.FirstOrderEMAInterpolator
import dev.nextftc.control.interpolators.FirstOrderEMAParameters
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe

class FirstOrderEMAInterpolatorTest : AnnotationSpec() {

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
        c1 shouldBe 2.5
        c2 shouldBe 3.75
        c3 shouldBe 4.375
        c4 shouldBe 4.6875
        c5 shouldBe 4.84375
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

        c1 shouldBe 2.421875
        c2 shouldBe 1.2109375
        c3 shouldBe 0.60546875
        c4 shouldBe 0.302734375
        c5 shouldBe 0.1513671875
    }
}
