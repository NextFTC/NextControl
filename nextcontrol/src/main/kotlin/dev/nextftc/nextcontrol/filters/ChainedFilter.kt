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
 * A filter that chains multiple filters together.
 *
 * @param filters The filters to chain together. Must have at least one filter.
 *
 * @author BeepBot99
 */
class ChainedFilter(vararg val filters: Filter) : Filter {

    init {
        require(filters.isNotEmpty()) { "ChainedFilter must have at least one filter" }
    }

    /**
     * Filters the given sensor measurement by passing it through all the filters in the chain in order.
     *
     * @param sensorMeasurement The sensor measurement to filter.
     * @return The filtered sensor measurement.
     */
    override fun filter(sensorMeasurement: Double): Double {
        return filters.fold(sensorMeasurement) { acc, filter -> filter.filter(acc) }
    }
}