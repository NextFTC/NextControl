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
 *
 * This file also contains code from WPILib.
 * WPILib code is used under the BSD-3 license,
 * found in the LICENSES/WPILib.md file.
 */
package dev.nextftc.control.interpolators

import dev.nextftc.control.KineticState
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt
import kotlin.math.withSign
import kotlin.time.ComparableTimeMark
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.TimeSource

/**
 * Constraints for a trapezoidal motion profile.
 *
 * @property maxVelocity The maximum velocity of the profile.
 * @property maxAcceleration The maximum acceleration of the profile.
 */
data class TrapezoidProfileConstraints(val maxVelocity: Double, val maxAcceleration: Double) {
    init {
        require(maxVelocity >= 0.0) { "Constraints must be non-negative" }
        require(maxAcceleration >= 0.0) { "Constraints must be non-negative" }
    }
}

/**
 * A trapezoidal motion profile generator.
 *
 * A trapezoidal motion profile is a velocity profile that accelerates at a constant rate,
 * maintains a constant velocity, then decelerates at a constant rate. This creates a
 * trapezoid shape when velocity is plotted over time.
 *
 * The profile handles truncated motion profiles (with nonzero initial or final velocity)
 * and profiles that never reach maximum velocity (triangular profiles).
 *
 * @param constraints The [TrapezoidProfileConstraints] that define the maximum velocity and
 *  acceleration for the profile.
 *
 * @author WPILib contributors, Zach Harel
 */
class TrapezoidProfile(private val constraints: TrapezoidProfileConstraints) {
    private var direction = 0
    
    private var currentState = KineticState.ZERO
    
    private var endAccel = 0.0
    private var endVel = 0.0
    private var endDecel = 0.0

    /**
     * The total time required to complete the motion profile.
     */
    val totalTime: Double
        get() = endDecel

    /**
     * Calculates the state of the profile at a given time.
     *
     * @param t The time since the beginning of the profile
     * @param current The current state of the system.
     * @param goal The desired goal state.
     *
     * @return The state of the profile at time [t].
     */
    fun calculate(t: Duration, current: KineticState, goal: KineticState): KineticState {
        direction = if (shouldFlipAcceleration(current, goal)) -1 else 1
        currentState = direct(current)
        val directGoal = direct(goal)

        val timeSeconds = t.toDouble(DurationUnit.SECONDS)

        if (abs(currentState.velocity) > constraints.maxVelocity) {
            currentState = currentState.copy(velocity = constraints.maxVelocity.withSign(currentState.velocity))
        }
    
        // Deal with a possibly truncated motion profile (with nonzero initial or
        // final velocity) by calculating the parameters as if the profile began and
        // ended at zero velocity
        val cutoffBegin = currentState.velocity / constraints.maxAcceleration
        val cutoffDistBegin = cutoffBegin * cutoffBegin * constraints.maxAcceleration / 2.0
    
        val cutoffEnd = directGoal.velocity / constraints.maxAcceleration
        val cutoffDistEnd = cutoffEnd * cutoffEnd * constraints.maxAcceleration / 2.0
    
        // Now we can calculate the parameters as if it was a full trapezoid instead
        // of a truncated one
        val fullTrapezoidDist =
            cutoffDistBegin + (directGoal.position - currentState.position) + cutoffDistEnd
        var accelerationTime = constraints.maxVelocity / constraints.maxAcceleration
    
        var fullSpeedDist =
            fullTrapezoidDist - accelerationTime * accelerationTime * constraints.maxAcceleration
    
        // Handle the case where the profile never reaches full speed
        if (fullSpeedDist < 0) {
            accelerationTime = sqrt(fullTrapezoidDist / constraints.maxAcceleration)
            fullSpeedDist = 0.0
        }
    
        endAccel = accelerationTime - cutoffBegin
        endVel = endAccel + fullSpeedDist / constraints.maxVelocity
        endDecel = endVel + accelerationTime - cutoffEnd

        val position: Double
        val velocity: Double
        val accel: Double
    
        if (timeSeconds < endAccel) {
            velocity = currentState.velocity + timeSeconds * constraints.maxAcceleration
            position = currentState.position + (currentState.velocity + timeSeconds * constraints.maxAcceleration / 2.0) * timeSeconds
            accel = constraints.maxAcceleration
        } else if (timeSeconds < endVel) {
            velocity = constraints.maxVelocity
            position = currentState.position +
                ((currentState.velocity + endAccel * constraints.maxAcceleration / 2.0) * endAccel
                        + constraints.maxVelocity * (timeSeconds - endAccel))
            accel = 0.0
        } else if (timeSeconds <= endDecel) {
            velocity = directGoal.velocity + (endDecel - timeSeconds) * constraints.maxAcceleration
            val timeLeft = endDecel - timeSeconds
            position = directGoal.position - (directGoal.velocity + timeLeft * constraints.maxAcceleration / 2.0) * timeLeft
            accel = -constraints.maxAcceleration
        } else {
            velocity = directGoal.velocity
            position = directGoal.position
            accel = 0.0
        }
    
        return direct(KineticState(position, velocity, accel))
    }

    /**
     * Calculates the time remaining until the profile reaches a target position.
     *
     * @param target The target position to reach.
     *
     * @return The time remaining until the target is reached, in seconds.
     */
    fun timeLeftUntil(target: Double): Double {
        val position = currentState.position * direction
        var velocity = currentState.velocity * direction
    
        var endAccel = endAccel * direction
        var endFullSpeed = endVel * direction - endAccel
    
        if (target < position) {
            endAccel = -endAccel
            endFullSpeed = -endFullSpeed
            velocity = -velocity
        }
    
        endAccel = max(endAccel, 0.0)
        endFullSpeed = max(endFullSpeed, 0.0)
    
        val acceleration = constraints.maxAcceleration
        val deceleration = -constraints.maxAcceleration
    
        val distToTarget = abs(target - position)
        if (distToTarget < 1e-6) {
            return 0.0
        }
    
        var accelDist = velocity * endAccel + 0.5 * acceleration * endAccel * endAccel
        
        val decelVelocity: Double = if (endAccel > 0) {
            sqrt(abs(velocity * velocity + 2 * acceleration * accelDist))
        } else {
            velocity
        }
    
        var fullSpeedDist = constraints.maxVelocity * endFullSpeed
        val decelDist: Double
    
        if (accelDist > distToTarget) {
            accelDist = distToTarget
            fullSpeedDist = 0.0
            decelDist = 0.0
        } else if (accelDist + fullSpeedDist > distToTarget) {
            fullSpeedDist = distToTarget - accelDist
            decelDist = 0.0
        } else {
            decelDist = distToTarget - fullSpeedDist - accelDist
        }
    
        val accelTime =
            ((-velocity + sqrt(abs(velocity * velocity + 2 * acceleration * accelDist)))
                    / acceleration)
    
        val decelTime =
            ((-decelVelocity
                    + sqrt(abs(decelVelocity * decelVelocity + 2 * deceleration * decelDist)))
                    / deceleration)
    
        val fullSpeedTime = fullSpeedDist / constraints.maxVelocity
    
        return accelTime + fullSpeedTime + decelTime
    }

    /**
     * Checks if the profile has finished at the given time.
     *
     * @param t The time since the beginning of the profile, in seconds.
     *
     * @return true if the profile has finished, false otherwise.
     */
    fun isFinished(t: Double): Boolean {
        return t >= totalTime
    }
    
    /**
     * Flips the sign of the velocity and position if the profile is inverted.
     * Used internally to handle backward motion.
     *
     * @param state The state to transform.
     *
     * @return The transformed state with signs adjusted based on [direction].
     */
    private fun direct(state: KineticState): KineticState {
        val position = state.position * direction
        val velocity = state.velocity * direction
        val acceleration = state.acceleration * direction
        return KineticState(position, velocity, acceleration)
    }
    
    companion object {
        /**
         * Determines if the acceleration should be flipped based on the initial and goal states.
         *
         * @param initial The initial state.
         * @param goal The goal state.
         *
         * @return true if the goal position is less than the initial position, false otherwise.
         */
        private fun shouldFlipAcceleration(initial: KineticState, goal: KineticState): Boolean {
            return initial.position > goal.position
        }
    }
}

/**
 * An [InterpolatorElement] that generates smooth trapezoidal motion profiles.
 *
 * This element uses a [TrapezoidProfile] to interpolate from the current reference to the goal
 * state while respecting velocity and acceleration constraints. The profile generates a smooth
 * trajectory that accelerates, maintains constant velocity, and decelerates.
 *
 * @param constraints The [TrapezoidProfileConstraints] that define the maximum velocity and
 *  acceleration for the profile.
 * @param timeSource The time source used to track elapsed time. Defaults to the system's
 *  monotonic time source.
 *
 * @author WPILib contributors, Zach Harel
 */
class TrapezoidProfileElement @JvmOverloads constructor(
    constraints: TrapezoidProfileConstraints,
    val timeSource: TimeSource.WithComparableMarks = TimeSource.Monotonic
) : InterpolatorElement {
    /**
     * The underlying trapezoidal profile generator.
     */
    internal val profile = TrapezoidProfile(constraints)

    /**
     * The goal that the interpolator is trying to reach. Setting this resets the profile's
     * start time.
     */
    override var goal = KineticState()
        set(value) {
            field = value
            lastTimestamp = timeSource.markNow()
        }

    internal lateinit var lastTimestamp: ComparableTimeMark

    /**
     * The previous reference state, used as the starting point for the next profile calculation.
     */
    internal var previousReference = KineticState.ZERO

    override val currentReference: KineticState
        get() {
            val nextTimestamp = timeSource.markNow()

            if (!::lastTimestamp.isInitialized) {
                lastTimestamp = nextTimestamp
                return KineticState()
            }

            val dt = nextTimestamp - lastTimestamp

            previousReference = profile.calculate(dt, previousReference, goal)
            return previousReference
        }

    override fun reset() {
        lastTimestamp = timeSource.markNow()
        previousReference = KineticState.ZERO
    }
}