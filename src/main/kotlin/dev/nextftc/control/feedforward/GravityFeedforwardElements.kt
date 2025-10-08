/*
 * Copyright (c) 2025. The NextFTC Team and other contributors.
 *
 * See the LICENSE in the root of this project for more information.
 */

package dev.nextftc.control.feedforward

import dev.nextftc.control.KineticState
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