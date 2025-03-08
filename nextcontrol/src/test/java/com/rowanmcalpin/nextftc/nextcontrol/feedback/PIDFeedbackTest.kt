package com.rowanmcalpin.nextftc.nextcontrol.feedback

import com.rowanmcalpin.nextftc.nextcontrol.utils.KineticState
import org.junit.Assert.assertEquals
import kotlin.test.Test

class PIDFeedbackTest {
    @Test
    fun `returns 0 when all gains are 0`(){
        // Arrange
        val posPID = PositionalPIDController(0.0, 0.0, 0.0)
        val velPID = VelocityPIDController(0.0, 0.0, 0.0)

        // Act
        val posFeedback = posPID.calculate(KineticState(20.0, 10.0))
        val velFeedback = velPID.calculate(KineticState(20.0, 10.0))

        // Assert
        assertEquals(0.0, posFeedback, 0.0)
        assertEquals(0.0, velFeedback, 0.0)
    }

    @Test
    fun `returns 0 when error is 0`(){
        // Arrange
        val posPID = PositionalPIDController(1.0, 1.0, 1.0)
        val velPID = VelocityPIDController(1.0, 1.0, 1.0)

        // Act
        val posFeedback = List<Double>(5) { posPID.calculate(KineticState(0.0, 0.0, 0.0)) }
        val velFeedback = List<Double>(5) { velPID.calculate(KineticState(0.0, 0.0, 0.0)) }

        // Assert
        posFeedback.forEach { assertEquals(0.0, it, 0.0) }
        velFeedback.forEach { assertEquals(0.0, it, 0.0) }
    }

    @Test
    fun `returns error when kP is 1 and other gains are 0`(){
        // Arrange
        val posPID = PositionalPIDController(1.0, 0.0, 0.0)
        val velPID = VelocityPIDController(1.0, 0.0, 0.0)
        val error = 10.0

        // Act
        val posFeedback = posPID.calculate(KineticState(10.0, 14.0))
        val velFeedback = velPID.calculate(KineticState(27.0, 10.0, 97.0))

        // Assert
        assertEquals(error, posFeedback, 0.0)
        assertEquals(error, velFeedback, 0.0)
    }

    @Test
    fun `integral sums correctly when kI is 1 and other gains are 0`(){
        //Arrange
        val posPID = PositionalPIDController(0.0, 1.0, 0.0)
        val velPID = VelocityPIDController(0.0, 1.0, 0.0)

        // Act
        val posFeedback = List<Double>(5) { index -> posPID.calculate(1 + index.toLong(), KineticState(10.0, 0.0, 0.0)) }
        val velFeedback = List<Double>(5) { index -> velPID.calculate(1 + index.toLong(), KineticState(0.0, 10.0, 0.0)) }

        // Assert
        posFeedback.forEachIndexed { index, it -> assertEquals(10.0 * index, it, 0.0) }
        velFeedback.forEachIndexed { index, it -> assertEquals(10.0 * index, it, 0.0) }
    }
}