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