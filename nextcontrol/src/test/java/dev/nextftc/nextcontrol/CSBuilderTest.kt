package dev.nextftc.nextcontrol

import dev.nextftc.nextcontrol.feedback.FeedbackElement
import dev.nextftc.nextcontrol.feedback.PIDCoefficients
import dev.nextftc.nextcontrol.feedback.PIDElement
import dev.nextftc.nextcontrol.feedback.PIDType
import dev.nextftc.nextcontrol.feedforward.FeedforwardElement
import dev.nextftc.nextcontrol.filters.FilterElement
import dev.nextftc.nextcontrol.interpolators.InterpolatorElement
import dev.nextftc.nextcontrol.utils.KineticState
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BuildControlSystemTest {

    @Test
    fun `buildControlSystem with interpolator sets correct components`() {
        // Arrange
        val interpolator = mockk<InterpolatorElement>()
        val kP = 1.0
        val kI = 0.1
        val kD = 0.01

        // Act
        val controlSystem = buildControlSystem(interpolator) {
            setPosPID(kP, kI, kD)
        }

        // Assert
        val feedbackField = controlSystem.javaClass.getDeclaredField("feedback")
        feedbackField.isAccessible = true
        val feedbackComponent = feedbackField.get(controlSystem)

        assertTrue(feedbackComponent is PIDElement)
        val pidElement = feedbackComponent as PIDElement
        assertEquals(PIDType.POSITIONAL, pidElement.type)
        assertEquals(kP, pidElement.coefficients.kP)
        assertEquals(kI, pidElement.coefficients.kI)
        assertEquals(kD, pidElement.coefficients.kD)

        val interpolatorField = controlSystem.javaClass.getDeclaredField("interpolator")
        interpolatorField.isAccessible = true
        val interpolatorComponent = interpolatorField.get(controlSystem)

        assertEquals(interpolator, interpolatorComponent)
    }

    @Test
    fun `buildControlSystem with goal sets correct components`() {
        // Arrange
        val goal = dev.nextftc.nextcontrol.utils.KineticState(10.0, 0.0, 0.0)
        val kP = 1.0
        val kI = 0.1
        val kD = 0.01

        // Act
        val controlSystem = buildControlSystem(goal) {
            setPosPID(kP, kI, kD)
        }

        // Assert
        val feedbackField = controlSystem.javaClass.getDeclaredField("feedback")
        feedbackField.isAccessible = true
        val feedbackComponent = feedbackField.get(controlSystem)

        assertTrue(feedbackComponent is PIDElement)
        val pidElement = feedbackComponent as PIDElement
        assertEquals(PIDType.POSITIONAL, pidElement.type)
        assertEquals(kP, pidElement.coefficients.kP)
        assertEquals(kI, pidElement.coefficients.kI)
        assertEquals(kD, pidElement.coefficients.kD)

        val interpolatorField = controlSystem.javaClass.getDeclaredField("interpolator")
        interpolatorField.isAccessible = true
        val interpolatorComponent = interpolatorField.get(controlSystem)

        assertTrue(interpolatorComponent is ConstantInterpolator)
        val interpolator = interpolatorComponent as ConstantInterpolator
        assertEquals(goal, interpolator.goal)
    }

    @Test
    fun `buildControlSystem can add multiple components`() {
        // Arrange
        val interpolator = mockk<InterpolatorElement>()
        val mockFeedforward = mockk<dev.nextftc.nextcontrol.feedforward.FeedforwardElement>()
        val mockFilter = mockk<FilterElement>()

        // Act
        val controlSystem = buildControlSystem(interpolator) {
            setPosPID(1.0, 0.1, 0.01)
            setFeedforward(mockFeedforward)
            setFilter(mockFilter)
        }

        // Assert
        val feedforwardField = controlSystem.javaClass.getDeclaredField("feedforward")
        feedforwardField.isAccessible = true
        val feedforwardComponent = feedforwardField.get(controlSystem)
        assertEquals(mockFeedforward, feedforwardComponent)

        val filterField = controlSystem.javaClass.getDeclaredField("filter")
        filterField.isAccessible = true
        val filterComponent = filterField.get(controlSystem)
        assertEquals(mockFilter, filterComponent)
    }

    @Test
    fun `buildControlSystem can set the velocity pid`(){
        //arrange
        val interpolator = mockk<InterpolatorElement>()
        val kp = 1.0
        val ki = 0.0
        val kd = 0.0

        //act
        val controlSystem = buildControlSystem(interpolator) {
            setVelPID(kp, ki, kd)
        }

        //assert
        val feedbackField = controlSystem.javaClass.getDeclaredField("feedback")
        feedbackField.isAccessible = true
        val feedbackComponent = feedbackField.get(controlSystem)

        assertTrue(feedbackComponent is PIDElement)
        val pidElement = feedbackComponent as PIDElement
        assertEquals(PIDType.VELOCITY, pidElement.type)
        assertEquals(kp, pidElement.coefficients.kP)
        assertEquals(ki, pidElement.coefficients.kI)
        assertEquals(kd, pidElement.coefficients.kD)
    }
}