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

package dev.nextftc.nextcontrol.feedforward

import dev.nextftc.nextcontrol.KineticState

/**
 * An element of a feedforward controller.
 * A feedforward controller is a controller that does not rely on sensor input, only information
 *  already known about the system. A feedforward controller uses a *model* of the system to
 *  "predict" the proper input to obtain a desired output.
 *
 * @author BeepBot99
 */
interface FeedforwardElement {

    /**
     * Calculates the power to apply to the system.
     *
     * @param reference The reference state of the system.
     * @return The power to apply to the system.
     */
    fun calculate(reference: KineticState): Double
}

/**
 * A [FeedforwardElement] that does nothing.
 * This can be useful when you want to rely on only feedback control, with no feedforward.
 *
 * @author BeepBot99
 */
class NullFeedforward : FeedforwardElement {

    /**
     * Calculates the power to apply to the system.
     *
     * @param reference The reference state of the system.
     * @return Zero, as this feedforward element does nothing.
     */
    override fun calculate(reference: KineticState): Double = 0.0
}
