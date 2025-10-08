/*
 * Copyright (c) 2025. The NextFTC Team and other contributors.
 *
 * See the LICENSE in the root of this project for more information.
 */

package dev.nextftc.control.feedback

import dev.nextftc.control.KineticState

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
 * @param parameters the [BangBangParameters] for this element
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