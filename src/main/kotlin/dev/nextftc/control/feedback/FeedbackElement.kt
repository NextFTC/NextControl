/*
 * Copyright (c) 2025. The NextFTC Team and other contributors.
 *
 * See the LICENSE in the root of this project for more information.
 */

package dev.nextftc.control.feedback

import dev.nextftc.control.KineticState

/**
 * An element of a feedback controller.
 * A feedback controller is a controller that changes its input based on the error in the system, or
 *  the difference between the current state and the desired state (the reference). A feedback
 *  controller attempts to bring the error in the system to zero.
 *
 * @author BeepBot99, rowan-mcalpin
 */
fun interface FeedbackElement {

    /**
     * Calculates the power to apply to the system.
     *
     * @param error The current error in the system.
     * @return The power to apply to the system.
     */
    fun calculate(error: KineticState): Double

    /**
     * Resets this element
     */
    fun reset() { }
}

/**
 * Which component a FeedbackElement operates on (position or velocity)
 *
 * @author Zach.Waffle, rowan-mcalpin
 */
enum class FeedbackType {
    POSITION, VELOCITY
}
