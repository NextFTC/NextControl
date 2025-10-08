/*
 * Copyright (c) 2025. The NextFTC Team and other contributors.
 *
 * See the LICENSE in the root of this project for more information.
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
