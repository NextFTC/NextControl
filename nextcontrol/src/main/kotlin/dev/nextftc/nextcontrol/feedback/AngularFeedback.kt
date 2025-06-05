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
import kotlin.math.PI
import org.apache.commons.math3.util.MathUtils.normalizeAngle

enum class AngleType(val halfRevolution: Double, val toRadians: Double) {
    RADIANS(PI, 1.0),
    DEGREES(180.0, PI / 180.0),
    REVOLUTIONS(0.5, 2 * PI);

    /**
     * Normalizes [angle] to the range [[halfRevolution], [halfRevolution]].
     *
     */
    fun normalize(angle: Double) =
        normalizeAngle(angle * toRadians, 0.0) / toRadians
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