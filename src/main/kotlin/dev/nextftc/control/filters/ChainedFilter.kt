/*
 * Copyright (c) 2025. The NextFTC Team and other contributors.
 *
 * See the LICENSE in the root of this project for more information.
 */

package dev.nextftc.control.filters

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