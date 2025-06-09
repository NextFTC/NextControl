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

/**
 * An element of a setpoint interpolator.
 * An interpolator element is given a goal and outputs a reference each loop.
 *
 * @author BeepBot99, rowan-mcalpin
 */
interface InterpolatorElement {

    /**
     * The goal that the interpolator is trying to reach
     */
    var goal: KineticState

    /**
     * The reference at the current time
     */
    val currentReference: KineticState

    /**
     * Resets this element
     */
    fun reset() { }
}

/**
 * An [InterpolatorElement] that doesn't interpolate and always returns the goal.
 *
 * @param goal The initial goal, usually zero.
 *
 * @author BeepBot99
 */
class ConstantInterpolator(override var goal: KineticState) : InterpolatorElement {

    /**
     * The reference at the current time, which is always equal to [goal].
     */
    override val currentReference: KineticState
        get() = goal
}
