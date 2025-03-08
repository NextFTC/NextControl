package com.rowanmcalpin.nextftc.nextcontrol.feedback

import com.rowanmcalpin.nextftc.nextcontrol.utils.KineticState
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class SquIDFeedbackTest {
    @Test
    fun `constructor overload functions as expected`() {
        // Arrange
        val coefsArguments = SquIDElement(PIDType.POSITIONAL, 1.0, 1.0, 1.0)
        val coefsObject = SquIDElement(PIDType.POSITIONAL, PIDCoefficients(1.0, 1.0, 1.0))

        //Assert
        assertEquals(coefsArguments.coefficients, coefsObject.coefficients)
    }

    @Test
    fun `returns 0 when all gains are 0`() {
        // Arrange
        val posSquID = SquIDElement(PIDType.POSITIONAL, 0.0, 0.0, 0.0)
        val velSquID = SquIDElement(PIDType.VELOCITY, 0.0, 0.0, 0.0)

        // Act
        val posFeedback = posSquID.calculate(KineticState(20.0, 10.0))
        val velFeedback = velSquID.calculate(KineticState(20.0, 10.0))

        // Assert
        assertEquals(0.0, posFeedback, 0.0)
        assertEquals(0.0, velFeedback, 0.0)
    }

    @Test
    fun `returns 0 when error is 0`() {
        // Arrange
        val posSquID = SquIDElement(PIDType.POSITIONAL, 1.0, 1.0, 1.0)
        val velSquID = SquIDElement(PIDType.VELOCITY, 1.0, 1.0, 1.0)

        // Act
        val posFeedback = List<Double>(5) { posSquID.calculate(KineticState(0.0, 0.0, 0.0)) }
        val velFeedback = List<Double>(5) { velSquID.calculate(KineticState(0.0, 0.0, 0.0)) }

        // Assert
        posFeedback.forEach { assertEquals(0.0, it, 0.0) }
        velFeedback.forEach { assertEquals(0.0, it, 0.0) }
    }

    @Test
    fun `returns sqrt of error when kP is 1 and other gains are 0`() {
        // Arrange
        val posSquID = SquIDElement(PIDType.POSITIONAL, 1.0, 0.0, 0.0)
        val velSquID = SquIDElement(PIDType.VELOCITY, 1.0, 0.0, 0.0)
        val error = 10.0

        // Act
        val posFeedback = posSquID.calculate(KineticState(10.0, 14.0))
        val velFeedback = velSquID.calculate(KineticState(27.0, 10.0, 97.0))

        // Assert
        assertEquals(sqrt(error), posFeedback, 0.0)
        assertEquals(sqrt(error), velFeedback, 0.0)
    }

    @Test
    fun `integral sums correctly when kI is 1 and other gains are 0`() {
        //Arrange
        val posSquID = SquIDElement(PIDType.POSITIONAL, 0.0, 1.0, 0.0)
        val velSquID = SquIDElement(PIDType.VELOCITY, 0.0, 1.0, 0.0)

        // Act
        val posFeedback = List<Double>(5) { index -> posSquID.calculate(1 + index.toLong(), KineticState(10.0, 0.0, 0.0)) }
        val velFeedback = List<Double>(5) { index -> velSquID.calculate(1 + index.toLong(), KineticState(0.0, 10.0, 0.0)) }

        // Assert
        posFeedback.forEachIndexed { index, it -> assertEquals(10.0 * index, it, 0.0) }
        velFeedback.forEachIndexed { index, it -> assertEquals(10.0 * index, it, 0.0) }
    }

    @Test
    fun `SquIDController constructor with individual gains functions as expected`() {
        // Arrange
        val kP = 2.0
        val kI = 0.5
        val kD = 0.1

        // Act
        val controller = SquIDController(kP, kI, kD)

        // Assert
        assertEquals(kP, controller.coefficients.kP, 0.0)
        assertEquals(kI, controller.coefficients.kI, 0.0)
        assertEquals(kD, controller.coefficients.kD, 0.0)
    }

    @Test
    fun `SquIDController constructor with PIDCoefficients functions as expected`() {
        // Arrange
        val coefficients = PIDCoefficients(3.0, 0.2, 0.05)

        // Act
        val controller = SquIDController(coefficients)

        // Assert
        assertEquals(coefficients.kP, controller.coefficients.kP, 0.0)
        assertEquals(coefficients.kI, controller.coefficients.kI, 0.0)
        assertEquals(coefficients.kD, controller.coefficients.kD, 0.0)
    }

    @Test
    fun `calculate returns expected value with non-zero gains`() {
        // Arrange
        val controller = SquIDController(1.0, 0.5, 0.2)
        val timestamp = 100L
        val posError = 5.0
        val velError = 2.0

        // Act
        val firstResult = controller.calculate(timestamp, posError, velError)
        val secondResult = controller.calculate(timestamp + 1, posError, velError)

        val firstExpected = sqrt(controller.coefficients.kP * posError)
        val secondExpected = sqrt(controller.coefficients.kP * posError) + controller.coefficients.kI * posError + controller.coefficients.kD * velError

        // Assert
        assertEquals(firstExpected, firstResult, 0.0)
        assertEquals(secondExpected, secondResult, 0.0)
    }

    @Test
    fun `calculate updates lastError and lastTimestamp`() {
        // Arrange
        val controller = SquIDController(1.0, 0.5, 0.2)
        val timestamp = 100L
        val posError = 5.0
        val velError = 2.0

        // Act
        controller.calculate(timestamp, posError, velError)

        // Assert
        assertEquals(posError, controller.lastError, 0.0)
        assertEquals(timestamp, controller.lastTimestamp)
    }

    @Test
    fun `reset clears errorSum and sets lastError and lastTimestamp to zero`() {
        // Arrange
        val controller = SquIDController(1.0, 0.5, 0.2)
        val timestamp = 100L
        val posError = 5.0
        val velError = 2.0
        controller.calculate(timestamp, posError, velError)
        assertNotEquals(0.0, controller.lastError)
        assertNotEquals(0L, controller.lastTimestamp)

        // Act
        controller.reset()

        // Assert
        assertEquals(0.0, controller.lastError, 0.0)
        assertEquals(0L, controller.lastTimestamp)
    }

    @Test
    fun `calculate integral contribution correctly with time passed`() {
        // Arrange
        val controller = SquIDController(0.0, 1.0, 0.0) // Only kI
        val initialTimestamp = System.nanoTime()
        val error = 10.0
        val dtNano = 1_000_000_000 // 1 second in nanoseconds

        // Act
        controller.calculate(initialTimestamp, error)
        val firstResult = controller.calculate(initialTimestamp + dtNano, error)
        val secondResult = controller.calculate(initialTimestamp + (dtNano * 2), error)

        // Assert
        assertEquals(0.0, controller.calculate(initialTimestamp, error), 0.0)
        assertEquals(10.0 * (dtNano.toDouble()), firstResult, 0.001)
        assertEquals(20.0 * (dtNano.toDouble()), secondResult, 0.001)
    }

    @Test
    fun `derivative term calculates correctly with velocity error`() {
        //Arrange
        val controller = SquIDController(0.0, 0.0, 1.0)
        val initialTimestamp = System.nanoTime()
        val velError = 10.0

        //Act
        val firstResult = controller.calculate(initialTimestamp, 0.0, velError)
        val secondResult = controller.calculate(initialTimestamp + 1, 0.0, velError)

        //Assert
        assertEquals(0.0, firstResult, 0.0)
        assertEquals(velError, secondResult, 0.0)
    }

    @Test
    fun `derivative term calculates correctly with position error over time`() {
        //Arrange
        val controller = SquIDController(0.0, 0.0, 1.0)
        val initialTimestamp = System.nanoTime()
        val firstPosError = 10.0
        val secondPosError = 20.0
        val dt = 1.0

        //Act
        controller.calculate(initialTimestamp, firstPosError, 0.0)
        val secondResult = controller.calculate(initialTimestamp + dt.toLong(), secondPosError, (secondPosError - firstPosError) / dt)

        //Assert
        assertEquals(10.0, secondResult, 0.0)
    }

    @Test
    fun `calculate uses velocity and acceleration in VELOCITY mode`() {
        // Arrange
        val pidElement = SquIDElement(PIDType.VELOCITY, 1.0, 0.5, 0.2)
        val kineticState = KineticState(position = 10.0, velocity = 5.0, acceleration = 2.0)

        // Act
        val result = pidElement.calculate(kineticState)

        // Assert
        assertEquals(sqrt(5.0), result, 0.0) //only kp is in effect since error sum and last error are 0.

    }

    @Test
    fun `calculate uses position and velocity in POSITIONAL mode`() {
        // Arrange
        val pidElement = SquIDElement(PIDType.POSITIONAL, 1.0, 0.5, 0.2)
        val kineticState = KineticState(position = 10.0, velocity = 5.0, acceleration = 2.0)

        //Act
        val result = pidElement.calculate(kineticState)

        //Assert
        assertEquals(sqrt(10.0), result, 0.0)
    }

    @Test
    fun `coefficients can be dynamically set in SquIDElement`() {
        //Arrange
        val pidElement = SquIDElement(PIDType.POSITIONAL, 1.0, 0.0, 0.0)
        val state = KineticState(10.0, 0.0, 0.0)

        //Act
        pidElement.coefficients = PIDCoefficients(0.0, 0.0, 0.0)
        val result = pidElement.calculate(state)

        //Assert
        assertEquals(0.0, result, 0.0)
    }

    @Test
    fun `different controllers don't interfere with each other`() {
        //Arrange
        val controller1 = SquIDController(0.0, 1.0, 0.0)
        val controller2 = SquIDController(0.0, 1.0, 0.0)

        //Act
        val timestamp = 10L
        controller1.calculate(timestamp, 10.0)
        val result1 = controller1.calculate(timestamp + 1, 10.0)
        val result2 = controller2.calculate(timestamp + 1, 10.0)

        //Assert
        assertEquals(10.0, result1, 0.001)
        assertEquals(0.0, result2, 0.001)
    }

    @Test
    fun `setPID actually changes coefficients`() {
        //Arrange
        val controller = SquIDController(1.0, 0.0, 0.0)

        //Act
        val result1 = controller.calculate(10.0)
        controller.setPID(2.0, 0.0, 0.0)
        val result2 = controller.calculate(10.0)

        //Assert
        assertEquals(sqrt(10.0), result1, 0.0)
        assertEquals(sqrt(20.0), result2, 0.0)
    }
}