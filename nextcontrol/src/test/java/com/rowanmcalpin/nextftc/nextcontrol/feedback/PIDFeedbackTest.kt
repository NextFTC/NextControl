package com.rowanmcalpin.nextftc.nextcontrol.feedback

import com.rowanmcalpin.nextftc.nextcontrol.utils.KineticState
import org.junit.Assert.assertEquals
import kotlin.test.Test

class PIDFeedbackTest {
    @Test
    fun `returns 0 when all gains are 0`(){
        // Arrange
        val pidFeedback = PIDElement(0.0, 0.0, 0.0)

        // Act
        val feedback = pidFeedback.calculate(KineticState(20.0, 10.0))

        // Assert
        assertEquals(0.0, feedback, 0.0)
    }

    @Test
    fun `returns 0 when error is 0`(){
        // Arrange
        val pidFeedback = PIDElement(1.0, 1.0, 1.0)

        // Act
        val feedback = pidFeedback.calculate(KineticState(0.0, 0.0))

        // Assert
        assertEquals(0.0, feedback, 0.0)
    }

    @Test
    fun `returns error when kP is 1 and other gains are 0`(){
        // Arrange
        val pidFeedback = PIDElement(1.0, 0.0, 0.0)
        val error = 10.0

        // Act
        val feedback = pidFeedback.calculate(KineticState(error, 0.0))

        // Assert
        assertEquals(error, feedback, 0.0)
    }
}