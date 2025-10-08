/*
 * Copyright (c) 2025. The NextFTC Team and other contributors.
 *
 * See the LICENSE in the root of this project for more information.
 */

package dev.nextftc.nextcontrol.feedforward

import dev.nextftc.control.KineticState
import dev.nextftc.control.feedforward.ElevatorFeedforward
import dev.nextftc.control.feedforward.GravityFeedforwardParameters
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe

class ElevatorFeedforwardTest : AnnotationSpec() {
    @Test
    fun `calculate returns 0 feedforward when parameters are 0`() {
        // Arrange
        val parameters = GravityFeedforwardParameters() // Default to 0
        val feedforward = ElevatorFeedforward(parameters)
        val reference = KineticState(1.0, 2.0, 3.0)

        // Act
        val actual = feedforward.calculate(reference)

        // Assert
        actual shouldBe 0.0
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
        actual shouldBe 7.0
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
        actual1 shouldBe 4.0
        actual2 shouldBe 8.0
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
        actual shouldBe -1.0
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
        actual shouldBe 1.0
    }
}