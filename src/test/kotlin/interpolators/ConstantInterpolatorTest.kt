/*
 * Copyright (c) 2025. The NextFTC Team and other contributors.
 *
 * See the LICENSE in the root of this project for more information.
 */

package dev.nextftc.nextcontrol.interpolators

import dev.nextftc.control.KineticState
import dev.nextftc.control.interpolators.ConstantInterpolator
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe

class ConstantInterpolatorTest : AnnotationSpec() {

    @Test
    fun `constructor parameter is set as goal`() {
        // Arrange
        val expected = KineticState(1.0, 2.0, 3.0)

        // Act
        val interpolator = ConstantInterpolator(expected)

        // Assert
        interpolator.goal shouldBe expected
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
        firstReference shouldBe firstGoal
        secondReference shouldBe secondGoal
    }
}