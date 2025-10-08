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