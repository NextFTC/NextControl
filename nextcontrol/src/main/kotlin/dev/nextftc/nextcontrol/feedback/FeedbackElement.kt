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

package dev.nextftc.nextcontrol.feedback

import dev.nextftc.nextcontrol.KineticState

/**
 * An element of a feedback controller.
 * A feedback controller is a controller that changes its input based on the error in the system, or
 *  the difference between the current state and the desired state (the reference). A feedback
 *  controller attempts to bring the error in the system to zero.
 *
 * @author BeepBot99
 */
interface FeedbackElement {

    /**
     * Calculates the power to apply to the system.
     *
     * @param error The current error in the system.
     * @return The power to apply to the system.
     */
    fun calculate(error: KineticState): Double
}

/**
 * A [FeedbackElement] that does nothing
 * This can be useful when you want to rely on only feedforward control, with no feedback.
 *
 * @author BeepBot99
 */
class NullFeedback : FeedbackElement {

    /**
     * Calculates the power to apply to the system.
     *
     * @param error The current error in the system.
     * @return Zero, as this feedback element does nothing.
     */
    override fun calculate(error: KineticState): Double = 0.0
}
