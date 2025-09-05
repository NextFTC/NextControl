package dev.nextftc.nextcontrol.feedback

import dev.nextftc.control.KineticState
import dev.nextftc.control.feedback.AngleType
import dev.nextftc.control.feedback.AngularFeedback
import dev.nextftc.control.feedback.FeedbackElement
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.math.PI

data class Angle(val unnormalized: Double, val normalized: Double, val type: AngleType)

class AngularFeedbackTest : FunSpec() {

    init {

        val data = listOf(
            Angle(359.0, -1.0, AngleType.DEGREES),
            Angle(170.0, 170.0, AngleType.DEGREES),
            Angle(-182.0, 178.0, AngleType.DEGREES),
            Angle(0.0, 0.0, AngleType.DEGREES),
            Angle(5 * PI / 4, -3 * PI / 4, AngleType.RADIANS),
            Angle(PI / 4, PI / 4, AngleType.RADIANS),
            Angle(-2 * PI, 0.0, AngleType.RADIANS),
            Angle(0.0, 0.0, AngleType.RADIANS),
            Angle(1.25, 0.25, AngleType.REVOLUTIONS),
            Angle(-0.25, -0.25, AngleType.REVOLUTIONS),
            Angle(-0.75, 0.25, AngleType.REVOLUTIONS),
            Angle(0.0, 0.0, AngleType.REVOLUTIONS)
        )

        context("correctly normalizes angle and returns feedbackElement output") {
            withData(data) { (unnormalized, normalized, type) ->
                // Arrange
                val feedbackElement = mockk<FeedbackElement>()
                every { feedbackElement.calculate(any()) } returns 15.0
                val angularFeedback = AngularFeedback(type, feedbackElement)
                val expectedOutput = 15.0

                // Act
                val output = angularFeedback.calculate(KineticState(unnormalized))

                // Assert
                output shouldBe expectedOutput
                verify(exactly = 1) {
                    feedbackElement.calculate(
                        withArg {
                            it.position shouldBe (normalized plusOrMinus 1e-6)
                        }
                    )
                }
            }
        }

        context("velocity and acceleration are not affected by angle normalization") {
            withData(data) { (unnormalized, normalized, type) ->
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
                            it.position shouldBe (expected.position plusOrMinus 1e-6)
                            it.velocity shouldBe expected.velocity
                            it.acceleration shouldBe expected.acceleration
                        }
                    )
                }
            }
        }
    }
}