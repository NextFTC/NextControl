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

package dev.nextftc.nextcontrol.feedforward

import dev.nextftc.nextcontrol.utils.KineticState
import kotlin.math.cos
import kotlin.math.sign

/**
 * Parameters for [ElevatorFeedforward] and [ArmFeedforward]
 *
 * @param kG gravity value, added to overcome gravity
 * @param kV velocity gain, multiplied by the target velocity
 * @param kA acceleration gain, multiplied by the target acceleration
 * @param kS static gain, used to overcome static friction (multiplied by the sign of velocity)
 */
data class GravityFeedforwardParameters @JvmOverloads constructor(
    @JvmField var kG: Double = 0.0,
    @JvmField var kV: Double = 0.0,
    @JvmField var kA: Double = 0.0,
    @JvmField var kS: Double = 0.0
)

/**
 * Elevator feedforward with velocity, acceleration, static, and gravity to model a vertical elevator
 *
 * @param parameters the [GravityFeedforwardParameters] for the feedforward gains
 *
 * @author rowan-mcalpin
 */
class ElevatorFeedforward(val parameters: GravityFeedforwardParameters) : FeedforwardElement {

    /**
     * Calculates the feedforward for a given reference
     *
     * @param reference the currently desired [KineticState] for the system
     */
    override fun calculate(reference: KineticState): Double {
        return parameters.kG +
                parameters.kV * reference.velocity +
                parameters.kA * reference.acceleration +
                parameters.kS * reference.velocity.sign
    }
}

/**
 * Arm feedforward with velocity, acceleration, static, and gravity to model a vertical elevator
 *
 * @param parameters the [GravityFeedforwardParameters] for the feedforward gains
 * @param positionToAngle function to convert a position into an angle in radians
 *
 * @author rowan-mcalpin
 */
class ArmFeedforward(val parameters: GravityFeedforwardParameters) : FeedforwardElement {

    /**
     * Calculates the feedforward for a given reference
     *
     * @param reference the currently desired [KineticState] for the system
     */
    override fun calculate(reference: KineticState): Double {
        return parameters.kG * cos(reference.position) +
                parameters.kV * reference.velocity +
                parameters.kA * reference.acceleration +
                parameters.kS * reference.velocity.sign
    }
}