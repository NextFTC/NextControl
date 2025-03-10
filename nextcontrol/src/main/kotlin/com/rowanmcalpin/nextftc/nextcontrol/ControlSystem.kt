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

package com.rowanmcalpin.nextftc.nextcontrol

import com.rowanmcalpin.nextftc.nextcontrol.feedback.FeedbackElement
import com.rowanmcalpin.nextftc.nextcontrol.feedforward.FeedforwardElement
import com.rowanmcalpin.nextftc.nextcontrol.filters.FilterElement
import com.rowanmcalpin.nextftc.nextcontrol.interpolators.InterpolatorElement
import kotlin.math.abs

/**
 * A robust controller for almost any system.
 * A [ControlSystem] consists of four "elements": feedback, feedforward, filter, and interpolator.
 *
 * Calculating a power involves five steps:
 * 1. filter the sensor measurement
 * 2. obtain the current reference
 * 3. calculate the feedback component by passing the error to the feedback element
 * 4. calculate the feedforward component by passing the current reference to the feedforward
 * element
 * 5. Return the sum of the feedback and feedforward components
 *
 * @param feedback The [FeedbackElement], which contributes a power to drive the error to zero.
 * @param feedforward The [FeedforwardElement], which contributes a power given only the current
 *  reference.
 * @param filter The [FilterElement], which removes noise from the sensor measurements.
 * @param interpolator The [InterpolatorElement], which interpolates goals into references.
 *
 * @author BeepBot99, rowan-mcalpin
 */
class ControlSystem(
    private val feedback: FeedbackElement,
    private val feedforward: FeedforwardElement,
    private val filter: FilterElement,
    private val interpolator: InterpolatorElement,
) {

    /**
     * The current goal of the system
     */
    var goal: KineticState by interpolator::goal

    /**
     * The last raw (unfiltered) measurement
     */
    var lastRawMeasurement: KineticState = KineticState();

    /**
     * The last filtered measurement
     */
    var lastFilteredMeasurement: KineticState = KineticState();

    /**
     * Calculates the output power given the current state of the system. In the case that your
     *  system is feedforward-only, leave the current state empty.
     *
     * @param sensorMeasurement The current state of the system, as measured by a sensor. Don't
     *  pass if no sensor is being used.
     *
     * @return The power to apply to the system
     */
    @JvmOverloads
    fun calculate(sensorMeasurement: KineticState = KineticState()): Double {
        val filteredMeasurement = filter.filter(sensorMeasurement)

        lastRawMeasurement = sensorMeasurement;
        lastFilteredMeasurement = filteredMeasurement;

        val error = interpolator.currentReference - filteredMeasurement

        val feedbackOutput = feedback.calculate(error)
        val feedforwardOutput = feedforward.calculate(interpolator.currentReference)

        return feedbackOutput + feedforwardOutput
    }

    /**
     * Whether the system is within a specified tolerance of the goal
     *
     * @param tolerance how close to the goal is considered within tolerance
     * @param useFilteredMeasurement whether to use a filtered or raw measurement
     *
     * @return whether the system is within tolerance of the goal
     *
     * @author rowan-mcalpin
     */
    @JvmOverloads
    fun isWithinTolerance(tolerance: Double, useFilteredMeasurement: Boolean = true): Boolean {
        return abs(
            (goal - (if (useFilteredMeasurement) lastFilteredMeasurement else
                lastRawMeasurement)).position
        ) <= tolerance;
    }
}

