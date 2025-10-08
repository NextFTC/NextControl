/*
 * Copyright (c) 2025. The NextFTC Team and other contributors.
 *
 * See the LICENSE in the root of this project for more information.
 */

package dev.nextftc.control.feedforward

import dev.nextftc.control.KineticState

/**
 * An element of a feedforward controller.
 * A feedforward controller is a controller that does not rely on sensor input, only information
 *  already known about the system. A feedforward controller uses a *model* of the system to
 *  "predict" the proper input to obtain a desired output.
 *
 * @author BeepBot99, rowan-mcalpin
 */
fun interface FeedforwardElement {

    /**
     * Calculates the power to apply to the system.
     *
     * @param reference The reference state of the system.
     * @return The power to apply to the system.
     */
    fun calculate(reference: KineticState): Double

    /**
     * Resets this element
     */
    fun reset() { }
}