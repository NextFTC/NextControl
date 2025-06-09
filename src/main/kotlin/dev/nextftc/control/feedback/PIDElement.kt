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

package dev.nextftc.control.feedback

import dev.nextftc.control.ControlSystem
import dev.nextftc.control.KineticState
import kotlin.math.sign
import kotlin.time.ComparableTimeMark
import kotlin.time.DurationUnit
import kotlin.time.TimeSource

/**
 * Coefficients for a [PIDElement].
 *
 * @param kP proportional gain, multiplied by the error
 * @param kI integral gain, multiplied by the integral of the error over time
 * @param kD derivative gain, multiplied by the derivative of the error
 */
data class PIDCoefficients @JvmOverloads constructor(
    @JvmField var kP: Double,
    @JvmField var kI: Double = 0.0,
    @JvmField var kD: Double = 0.0
)

/**
 * Traditional proportional-integral-derivative controller.
 *
 * @param coefficients the [PIDCoefficients] that contains the PID gains
 *
 * @author Zach.Waffle, rowan-mcalpin
 */
internal class PIDController @JvmOverloads constructor(
    val coefficients: PIDCoefficients,
    val resetIntegralOnZeroCrossover: Boolean = true
) {

    private var lastError: Double = 0.0
    private var errorSum = 0.0
    private var lastTimestamp: ComparableTimeMark? = null

    /**
     * Calculates the PID output
     *
     * @param timestamp the current time
     * @param positionError the error in position; the difference between the desired
     *  position and the current position
     * @param velocityError the error in velocity; the difference between the desired velocity
     *  and the current velocity
     *
     * @return the PID output
     */
    fun calculate(
        timestamp: ComparableTimeMark,
        positionError: Double,
        velocityError: Double
    ): Double {

        if (lastTimestamp == null) {
            lastError = positionError
            lastTimestamp = timestamp
        }

        if (resetIntegralOnZeroCrossover && lastError.sign != positionError.sign) {
            errorSum = 0.0
        }

        val deltaT = (timestamp - lastTimestamp!!).toDouble(DurationUnit.NANOSECONDS)
        errorSum += positionError * deltaT

        lastError = positionError
        lastTimestamp = timestamp

        return coefficients.kP * positionError + coefficients.kI * errorSum + coefficients.kD *
                velocityError
    }

    /**
     * Resets the PID controller
     */
    fun reset() {
        errorSum = 0.0
        lastError = 0.0
        lastTimestamp = null
    }
}

/**
 * A [FeedbackElement] that wraps a [PIDController] for use in a [ControlSystem]
 *
 * @param coefficients The [PIDCoefficients] that contains the PID gains
 *
 * @author Zach.Waffle, rowan-mcalpin
 */
class PIDElement @JvmOverloads constructor(
    private val pidType: FeedbackType,
    coefficients: PIDCoefficients,
    resetIntegralOnZeroCrossover: Boolean = true,
    private val timeSource: TimeSource.WithComparableMarks = TimeSource.Monotonic
) : FeedbackElement {

    @JvmOverloads
    constructor(pidType: FeedbackType, kP: Double, kI: Double = 0.0, kD: Double = 0.0) :
            this(pidType, PIDCoefficients(kP, kI, kD))

    private val controller = PIDController(coefficients, resetIntegralOnZeroCrossover)

    val coefficients by controller::coefficients
    override fun calculate(error: KineticState) = when (pidType) {
        FeedbackType.POSITION -> controller.calculate(timeSource.markNow(), error.position, error.velocity)
        FeedbackType.VELOCITY -> controller.calculate(timeSource.markNow(), error.velocity, error.acceleration)
    }

    override fun reset() {
        controller.reset()
    }
}