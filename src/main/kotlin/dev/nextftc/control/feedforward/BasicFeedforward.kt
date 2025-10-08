/*
 * Copyright (c) 2025. The NextFTC Team and other contributors.
 *
 * See the LICENSE in the root of this project for more information.
 */

package dev.nextftc.control.feedforward

import dev.nextftc.control.KineticState
import kotlin.math.sign

/**
 * Parameters for a [BasicFeedforward]
 *
 * @param kV velocity gain, multiplied by the reference velocity
 * @param kA acceleration gain, multiplied by the reference acceleration
 * @param kS static gain, used to overcome static friction (multiplied by the sign of velocity)
 *
 * @author rowan-mcalpin
 */
data class BasicFeedforwardParameters @JvmOverloads constructor(
    @JvmField var kV: Double = 0.0,
    @JvmField var kA: Double = 0.0,
    @JvmField var kS: Double = 0.0
)

/**
 * Basic feedforward with velocity, acceleration, and static to model a motor without any
 * forces other than static friction and inertia
 *
 * @param parameters the [BasicFeedforwardParameters] for the feedforward gains
 *
 * @author rowan-mcalpin
 */
class BasicFeedforward(val parameters: BasicFeedforwardParameters) :
    FeedforwardElement {

    @JvmOverloads
    constructor(kV: Double = 0.0, kA: Double = 0.0, kS: Double = 0.0) : this(
        BasicFeedforwardParameters(
            kV,
            kA,
            kS
        )
    )

    /**
     * Calculates the feedforward for a given reference
     *
     * @param reference the currently desired [KineticState] for the system
     */
    override fun calculate(reference: KineticState): Double {
        return parameters.kV * reference.velocity +
                parameters.kA * reference.acceleration +
                parameters.kS * reference.velocity.sign
    }
}