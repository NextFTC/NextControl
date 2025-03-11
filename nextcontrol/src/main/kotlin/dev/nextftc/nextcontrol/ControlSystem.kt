/*
 * NextFTC: a user-friendly control library for FIRST Tech Challenge
 *     Copyright (C) 2025 Rowan McAlpin
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package dev.nextftc.nextcontrol

import dev.nextftc.nextcontrol.builder.ControlSystemBuilder
import dev.nextftc.nextcontrol.filters.FilterElement
import dev.nextftc.nextcontrol.interpolators.InterpolatorElement

class ControlSystem(
    private val feedback: dev.nextftc.nextcontrol.feedback.FeedbackElement,
    private val feedforward: dev.nextftc.nextcontrol.feedforward.FeedforwardElement,
    private val filter: FilterElement,
    private val interpolator: InterpolatorElement,
) {

    var goal: dev.nextftc.nextcontrol.utils.KineticState by interpolator::goal

    @JvmOverloads
    fun calculate(sensorMeasurement: dev.nextftc.nextcontrol.utils.KineticState = dev.nextftc.nextcontrol.utils.KineticState()): Double {
        val filteredMeasurement = filter.filter(sensorMeasurement)

        val error = interpolator.currentReference - filteredMeasurement

        val feedbackOutput = feedback.calculate(error)
        val feedforwardOutput = feedforward.calculate(interpolator.currentReference)

        return feedbackOutput + feedforwardOutput
    }

    companion object {

        @JvmStatic
        fun builder() = ControlSystemBuilder()
    }
}