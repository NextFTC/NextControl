package dev.nextftc.nextcontrol.feedback

import dev.nextftc.nextcontrol.KineticState
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(Parameterized::class)
class AngularFeedbackTest(
    private val unnormalized: Double,
    private val normalized: Double,
    private val type: AngleType
) {

    companion object {

        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> {
            return listOf(
                arrayOf(359.0, -1.0, AngleType.DEGREES),
                arrayOf(170.0, 170.0, AngleType.DEGREES),
                arrayOf(-182.0, 178.0, AngleType.DEGREES),
                arrayOf(0.0, 0.0, AngleType.DEGREES),
                arrayOf(5 * PI / 4, -3 * PI / 4, AngleType.RADIANS),
                arrayOf(PI / 4, PI / 4, AngleType.RADIANS),
                arrayOf(-2 * PI, 0.0, AngleType.RADIANS),
                arrayOf(0.0, 0.0, AngleType.RADIANS),
                arrayOf(1.25, 0.25, AngleType.REVOLUTIONS),
                arrayOf(-0.25, -0.25, AngleType.REVOLUTIONS),
                arrayOf(-0.75, 0.25, AngleType.REVOLUTIONS),
                arrayOf(0.0, 0.0, AngleType.REVOLUTIONS)
            )
        }
    }

    @Test
    fun `correctly normalizes angle and returns feedbackElement output`() {
        // Arrange
        val feedbackElement = mockk<FeedbackElement>()
        every { feedbackElement.calculate(any()) } returns 15.0
        val angularFeedback = AngularFeedback(type, feedbackElement)
        val expectedOutput = 15.0

        // Act
        val output = angularFeedback.calculate(KineticState(unnormalized))

        // Assert
        assertEquals(expectedOutput, output, 0.0)
        verify(exactly = 1) {
            feedbackElement.calculate(
                withArg {
                    assertEquals(normalized, it.position, 1e-6)
                }
            )
        }
    }

    @Test
    fun `velocity and acceleration are not affected by angle normalization`() {
        // Arrange
        val error = KineticState(unnormalized, 10.0, 20.0)
        val expected = KineticState(normalized, 10.0, 20.0)
        val feedbackElement = mockk<FeedbackElement>()
        every { feedbackElement.calculate(any()) } returns 0.0
        val angularFeedback = AngularFeedback(type, feedbackElement)

        // Act
        angularFeedback.calculate(error)

        // Assert
        verify(exactly = 1) {
            feedbackElement.calculate(
                withArg {
                    assertEquals(expected.position, it.position, 1e-6)
                    assertEquals(expected.velocity, it.velocity, 0.0)
                    assertEquals(expected.acceleration, it.acceleration, 0.0)
                }
            )
        }
    }
}