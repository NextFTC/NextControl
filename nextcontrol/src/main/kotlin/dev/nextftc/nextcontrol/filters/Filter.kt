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

package dev.nextftc.nextcontrol.filters

/**
 * A filter that can be applied to a sensor measurement.
 *
 * @author BeepBot99, rowan-mcalpin
 */
fun interface Filter {

    /**
     * Filters the given sensor measurement.
     *
     * @param sensorMeasurement The sensor measurement to filter.
     *
     * @return The filtered sensor measurement.
     */
    fun filter(sensorMeasurement: Double): Double

    /**
     * Resets this filter
     */
    fun reset() { }
}