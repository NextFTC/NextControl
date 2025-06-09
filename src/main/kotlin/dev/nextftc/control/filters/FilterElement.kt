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

import dev.nextftc.control.KineticState

/**
 * An element that filters the position, velocity, and acceleration of a [KineticState].
 *
 * @param positionFilter the [Filter] to apply to the position
 * @param velocityFilter the [Filter] to apply to the velocity
 * @param accelerationFilter the [Filter] to apply to the acceleration
 *
 * @author BeepBot99, rowan-mcalpin
 */
class FilterElement @JvmOverloads constructor(
    private val positionFilter: Filter = Filter { it },
    private val velocityFilter: Filter = Filter { it },
    private val accelerationFilter: Filter = Filter { it }
) {

    /**
     * Filters the given [sensorMeasurement] using the configured filters.
     *
     * @param sensorMeasurement the [KineticState] to filter
     *
     * @return the filtered [KineticState]
     */
    fun filter(sensorMeasurement: KineticState): KineticState {
        return KineticState(
            positionFilter.filter(sensorMeasurement.position),
            velocityFilter.filter(sensorMeasurement.velocity),
            accelerationFilter.filter(sensorMeasurement.acceleration)
        )
    }

    fun reset() {
        positionFilter.reset()
        velocityFilter.reset()
        accelerationFilter.reset()
    }
}