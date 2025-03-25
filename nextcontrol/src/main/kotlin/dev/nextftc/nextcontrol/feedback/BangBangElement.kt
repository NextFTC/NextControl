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
import kotlin.math.sign

/**
 * Parameters for a [BangBangElement]
 *
 * @param gain the constant that is outputted when the error is positive (negated when error is negative)
 * @param hysteresis how close to 0 is considered 0
 */
data class BangBangParameters @JvmOverloads constructor(
    @JvmField var gain: Double,
    @JvmField var hysteresis: Double = 5.0
)

/**
 * A [FeedbackElement] that is a Bang Bang controller (when error is positive, output 1, when error is negative, output
 *  -1, when error is 0, output 0.
 *
 * @param feedbackType The type of component this controller operates on
 * @param gain the constant that the output is multiplied by
 *
 * @author rowan-mcalpin
 */
class BangBangElement(
    private val feedbackType: FeedbackType,
    private val parameters: BangBangParameters
) : FeedbackElement {

    override fun calculate(error: KineticState): Double {
        val feedbackError = when (feedbackType) {
            FeedbackType.POSITION -> error.position
            FeedbackType.VELOCITY -> error.velocity
        }
        return when {
            feedbackError >= parameters.hysteresis -> parameters.gain
            feedbackError <= -parameters.hysteresis -> -parameters.gain
            else -> 0.0
        }
    }
}