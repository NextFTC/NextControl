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

package dev.nextftc.control.filters

data class LowPassParameters @JvmOverloads constructor(
    @JvmField var alpha: Double,
    @JvmField var startingEstimate: Double = 0.0
)

/**
 * A simple low pass filter.
 *
 * High values of alpha are smoother but have more phase lag, low values of alpha allow more noise
 * but
 * will respond faster to quick changes in the measured state.
 *
 * @param parameters The parameters to use to configure the filter
 */
class LowPassFilter(val parameters: LowPassParameters) :
    Filter {

    /**
     * @param alpha The low pass gain. Must be between 0 and 1.
     * @param startingEstimate The initial estimate. Should be equal to the starting position. Is only read on class initialization.
     */
    @JvmOverloads
    constructor(alpha: Double, startingEstimate: Double = 0.0) : this(
        LowPassParameters(
            alpha,
            startingEstimate
        )
    )

    init {
        require(parameters.alpha in 0.0..1.0) {
            "Low pass gain must be between 0 and 1, but was ${parameters.alpha}"
        }
    }

    var previousEstimate = parameters.startingEstimate

    /**
     * Low Pass Filter estimate
     * @param sensorMeasurement unfiltered sensor reading
     * @return filtered estimate
     */
    override fun filter(sensorMeasurement: Double): Double {
        val estimate = parameters.alpha * previousEstimate + (1 - parameters.alpha) *
                sensorMeasurement
        previousEstimate = estimate
        return estimate
    }
}