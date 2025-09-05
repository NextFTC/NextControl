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
import kotlin.math.abs
import kotlin.math.sign
import kotlin.math.sqrt
import kotlin.time.ComparableTimeMark
import kotlin.time.DurationUnit
import kotlin.time.TimeSource

/**
 * Square-root P + ID (aka SquID) controller.
 *
 * @param coefficients the [PIDCoefficients] that contains the PID gains
 *
 * @author Zach.Waffle, rowan-mcalpin
 */
internal class SquIDController(val coefficients: PIDCoefficients) {

    private var lastError: Double = 0.0
    private var errorSum = 0.0
    private var lastTimestamp: ComparableTimeMark? = null

    /**
     * Calculates the SquID output
     *
     * @param timestamp the current time
     * @param positionError the error in position; the difference between the desired
     *  position and the current position
     * @param velocityError the error in velocity; the difference between the desired velocity
     *  and the current velocity
     *
     * @return the SquID output
     */
    fun calculate(
        timestamp: ComparableTimeMark,
        positionError: Double,
        velocityError: Double,
    ): Double {
        if (lastTimestamp == null) {
            lastError = positionError
            lastTimestamp = timestamp
        }

        val deltaT = (timestamp - lastTimestamp!!).toDouble(DurationUnit.NANOSECONDS)
        errorSum += (positionError) * deltaT

        lastError = positionError
        lastTimestamp = timestamp

        return sqrt(abs(coefficients.kP * positionError)) * positionError.sign + coefficients.kI * errorSum +
                coefficients.kD * velocityError
    }

    /**
     * Resets the SquID controller
     */
    fun reset() {
        errorSum = 0.0
        lastError = 0.0
        lastTimestamp = null
    }
}

/**
 * A [FeedbackElement] that wraps a [SquIDController] for use in a [ControlSystem]
 *
 * @param coefficients The [PIDCoefficients] that contains the PID gains
 *
 * @author Zach.Waffle, rowan-mcalpin
 */
class SquIDElement @JvmOverloads constructor(
    private val pidType: FeedbackType,
    coefficients: PIDCoefficients,
    private val timeSource: TimeSource.WithComparableMarks = TimeSource.Monotonic
) : FeedbackElement {

    @JvmOverloads
    constructor(pidType: FeedbackType, kP: Double, kI: Double = 0.0, kD: Double = 0.0) :
            this(pidType, PIDCoefficients(kP, kI, kD))

    private val controller = SquIDController(coefficients)

    val coefficients by controller::coefficients

    override fun calculate( error: KineticState) = when (pidType) {
        FeedbackType.POSITION -> controller.calculate(timeSource.markNow(), error.position, error.velocity)
        FeedbackType.VELOCITY -> controller.calculate(timeSource.markNow(), error.velocity, error.acceleration)
    }

    override fun reset() {
        controller.reset()
    }
}