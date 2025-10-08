/*
 * Copyright (c) 2025. The NextFTC Team and other contributors.
 *
 * See the LICENSE in the root of this project for more information.
 */

package dev.nextftc.control.interpolators

import dev.nextftc.control.KineticState

data class FirstOrderEMAParameters @JvmOverloads constructor(
    @JvmField var alpha: Double,
    @JvmField var startingReference: KineticState = KineticState()
)

/**
 * An [InterpolatorElement] that smoothly interpolates to the goal in order to smooth out setpoint
 * changes
 *
 * Uses a first-order EMA (exponential moving average) setpoint filter
 *
 * @param parameters the parameters to use
 *
 * @author rowan-mcalpin
 */
class FirstOrderEMAInterpolator(val parameters: FirstOrderEMAParameters) : InterpolatorElement {

    init {
        require(parameters.alpha in 0.0..1.0) {
            "Alpha must be between 0 and 1, but was $parameters.alpha"
        }
    }

    override var goal: KineticState = parameters.startingReference

    private var lastReference: KineticState = parameters.startingReference

    override val currentReference: KineticState
        get() {
            lastReference = goal * parameters.alpha + lastReference * (1 - parameters.alpha)
            return lastReference
        }
}