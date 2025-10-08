/*
 * Copyright (c) 2025. The NextFTC Team and other contributors.
 *
 * See the LICENSE in the root of this project for more information.
 */

package dev.nextftc.control.feedback

import dev.nextftc.control.KineticState
import kotlin.math.PI

enum class AngleType(val halfRevolution: Double, val toRadians: Double) {
    RADIANS(PI, 1.0),
    DEGREES(180.0, PI / 180.0),
    REVOLUTIONS(0.5, 2 * PI);

    /**
     * Normalizes [angle] to the range [[halfRevolution], [halfRevolution]].
     *
     */
    fun normalize(angle: Double) =
        ((angle + halfRevolution) % (2 * halfRevolution) + 2 *
                halfRevolution) % (2 *
                halfRevolution) -
                halfRevolution
}

/**
 * A [FeedbackElement] that wraps another [FeedbackElement] for angular positions.
 */
class AngularFeedback(private val type: AngleType, private val feedbackElement: FeedbackElement) :
    FeedbackElement {

    /**
     * Calculates the power to apply to the system.
     *
     * @param error The current error in the system.
     * @return The power to apply to the system.
     */
    override fun calculate(error: KineticState): Double {
        return feedbackElement.calculate(
            error.copy(position = type.normalize(error.position))
        )
    }
}