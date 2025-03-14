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

package dev.nextftc.nextcontrol.interpolators

import dev.nextftc.nextcontrol.KineticState
import dev.nextftc.nextcontrol.filters.LowPassParameters

/**
 * This smoothly interpolates to the goal in order to smooth out setpoint changes
 *
 * @param alpha how much the interpolator relies on the goal
 *
 * @author rowan-mcalpin
 */
class FirstOrderLagInterpolator(val parameters: LowPassParameters): InterpolatorElement {
    init {
        require(parameters.alpha in 0.0..1.0) {
            "Lag interpolator gain must be between 0 and 1, but was $parameters.alpha"
        }
    }

    override var goal: KineticState = KineticState()

    private var lastReference: KineticState = KineticState()

    override val currentReference: KineticState
        get() {
            lastReference = goal * parameters.alpha + lastReference * (1 - parameters.alpha)
            return lastReference
        }
}