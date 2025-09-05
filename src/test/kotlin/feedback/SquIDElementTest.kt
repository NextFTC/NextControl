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

package dev.nextftc.nextcontrol.feedback

import dev.nextftc.control.KineticState
import dev.nextftc.control.feedback.FeedbackType
import dev.nextftc.control.feedback.PIDCoefficients
import dev.nextftc.control.feedback.SquIDElement
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import kotlin.math.sqrt
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TestTimeSource

class SquIDElementTest : AnnotationSpec() {

    @Test
    fun `passing individual values creates a PIDCoefficients instance with the proper values`() {
        // Arrange
        val kP = 1.0
        val kI = 2.0
        val kD = 3.0
        val expected = PIDCoefficients(kP, kI, kD)

        // Act
        val squidElement = SquIDElement(FeedbackType.POSITION, kP, kI, kD)
        val actual = squidElement.coefficients

        // Assert
        actual shouldBe expected
    }

    @Test
    fun `returns zero when all gains are zero`() {
        // Arrange
        val coefficients = PIDCoefficients(0.0, 0.0, 0.0)
        val positionSquID = SquIDElement(FeedbackType.POSITION, coefficients)
        val velocitySquID = SquIDElement(FeedbackType.VELOCITY, coefficients)
        val input = KineticState(10.0, 20.0, 30.0)
        val expected = 0.0

        // Act
        val positionActual = positionSquID.calculate(input)
        val velocityActual = velocitySquID.calculate(input)

        // Assert
        positionActual shouldBe expected
        velocityActual shouldBe expected
    }

    @Test
    fun `returns zero when error has always been zero`() {
        // Arrange
        val coefficients = PIDCoefficients(1.0, 2.0, 3.0)
        val positionSquID = SquIDElement(FeedbackType.POSITION, coefficients)
        val velocitySquID = SquIDElement(FeedbackType.VELOCITY, coefficients)
        val input = KineticState(0.0, 0.0, 0.0)
        val expected = 0.0

        // Act
        val firstPositionActual = positionSquID.calculate(input)
        val secondPositionActual = positionSquID.calculate(input)
        val firstVelocityActual = velocitySquID.calculate(input)
        val secondVelocityActual = velocitySquID.calculate(input)

        // Assert
        firstPositionActual shouldBe expected
        secondPositionActual shouldBe expected
        firstVelocityActual shouldBe expected
        secondVelocityActual shouldBe expected
    }

    @Test
    fun `returns square root of error when kP is one and kI and kD are zero`() {
        // Arrange
        val positionSquID = SquIDElement(FeedbackType.POSITION, 1.0, 0.0, 0.0)
        val velocitySquID = SquIDElement(FeedbackType.VELOCITY, 1.0, 0.0, 0.0)
        val error = 10.0
        val positionInput = KineticState(error, 20.0, 30.0)
        val velocityInput = KineticState(20.0, error, 30.0)
        val expected = sqrt(error)

        // Act
        val positionActual = positionSquID.calculate(positionInput)
        val velocityActual = velocitySquID.calculate(velocityInput)

        // Assert
        positionActual shouldBe expected
        velocityActual shouldBe expected
    }

    @Test
    fun `returns sum of all past errors times deltaT when kI is one and kP and kD are zero`() {
        // Arrange
        val coefficients = PIDCoefficients(0.0, 1.0, 0.0)

        val timeSource = TestTimeSource()

        val positionSquID = SquIDElement(FeedbackType.POSITION, coefficients, timeSource = timeSource)
        val velocitySquID = SquIDElement(FeedbackType.VELOCITY, coefficients, timeSource = timeSource)

        val firstError = 10.0
        val firstTimespan = 1000.milliseconds
        val secondError = 20.0
        val secondTimespan = 2000.milliseconds
        val thirdError = 30.0
        val thirdTimespan = 3000.milliseconds

        // Since integral is estimated as a Riemann sum with right endpoints, the first error
        // does not contribute to the integral
        val expected = secondError * secondTimespan.inWholeNanoseconds + thirdError *
                thirdTimespan.inWholeNanoseconds

        // Act
        timeSource += firstTimespan
        positionSquID.calculate(KineticState(firstError, 5.0, 25.0))
        velocitySquID.calculate(KineticState(5.0, firstError, 25.0))

        timeSource += secondTimespan
        positionSquID.calculate(KineticState(secondError, 5.0, 25.0))
        velocitySquID.calculate(KineticState(5.0, secondError, 25.0))

        timeSource += thirdTimespan
        val positionActual = positionSquID.calculate(KineticState(thirdError, 5.0, 25.0))
        val velocityActual = velocitySquID.calculate(KineticState(5.0, thirdError, 25.0))

        // Assert
        positionActual shouldBe expected
        velocityActual shouldBe expected
    }

    @Test
    fun `returns error of derivative when kD is one and kP and kI are zero`() {
        // Arrange
        val coefficients = PIDCoefficients(0.0, 0.0, 1.0)
        val positionSquID = SquIDElement(FeedbackType.POSITION, coefficients)
        val velocitySquID = SquIDElement(FeedbackType.VELOCITY, coefficients)
        val derivativeError = 20.0
        val positionSquIDError = KineticState(10.0, derivativeError, 30.0)
        val velocitySquIDError = KineticState(10.0, 30.0, derivativeError)
        val expected = 20.0

        // Act
        val positionActual = positionSquID.calculate(positionSquIDError)
        val velocityActual = velocitySquID.calculate(velocitySquIDError)

        // Assert
        positionActual shouldBe expected
        velocityActual shouldBe expected
    }

    @Test
    fun `returns kP times square root of error plus kI times integral plus kD times derivative`() {
        // Arrange
        val kP = 5.0
        val kI = 6.0
        val kD = 7.0
        val coefficients = PIDCoefficients(kP, kI, kD)

        val timeSource = TestTimeSource()

        val positionSquID = SquIDElement(FeedbackType.POSITION, coefficients, timeSource = timeSource)
        val velocitySquID = SquIDElement(FeedbackType.VELOCITY, coefficients, timeSource = timeSource)

        val firstError = 20.0
        val firstTimespan = 1000.milliseconds
        val secondError = 10.0
        val secondTimespan = 1500.milliseconds
        val derivativeError = 10.0
        // Since integral is estimated as a Riemann sum with right endpoints, the first error
        // does not contribute to the integral
        val expectedIntegral = secondError * secondTimespan.inWholeNanoseconds
        val expected = sqrt(kP * secondError) + kI * expectedIntegral + kD * derivativeError

        // Act
        timeSource += firstTimespan
        positionSquID.calculate(KineticState(firstError, 0.0, 25.0))
        velocitySquID.calculate(KineticState(25.0, firstError, 0.0))

        timeSource += secondTimespan
        val positionActual =
            positionSquID.calculate(KineticState(secondError, derivativeError, 25.0))
        val velocityActual =
            velocitySquID.calculate(KineticState(25.0, secondError, derivativeError))

        // Assert
        positionActual shouldBe expected
        velocityActual shouldBe expected
    }

    @Test
    fun `acceleration error does not affect position squid`() {
        // Arrange
        val coefficients = PIDCoefficients(1.0, 2.0, 3.0)

        val timeSource = TestTimeSource()
        timeSource += 1000.milliseconds

        val firstController = SquIDElement(FeedbackType.POSITION, coefficients, timeSource = timeSource)
        val secondController = SquIDElement(FeedbackType.POSITION, coefficients, timeSource = timeSource)
        val firstError = KineticState(10.0, 20.0, 30.0)
        val secondError = KineticState(10.0, 20.0, 40.0)

        // Act
        val firstActual = firstController.calculate(firstError)
        val secondActual = secondController.calculate(secondError)

        // Assert
        firstActual shouldBe secondActual
    }

    @Test
    fun `position error does not affect velocity squid`() {
        // Arrange
        val coefficients = PIDCoefficients(1.0, 2.0, 3.0)

        val timeSource = TestTimeSource()
        timeSource += 1000.milliseconds

        val firstController = SquIDElement(FeedbackType.VELOCITY, coefficients, timeSource = timeSource)
        val secondController = SquIDElement(FeedbackType.VELOCITY, coefficients, timeSource = timeSource)
        val firstError = KineticState(10.0, 20.0, 30.0)
        val secondError = KineticState(15.0, 20.0, 30.0)

        // Act
        val firstActual = firstController.calculate(firstError)
        val secondActual = secondController.calculate(secondError)

        // Assert
        firstActual shouldBe secondActual
    }
}