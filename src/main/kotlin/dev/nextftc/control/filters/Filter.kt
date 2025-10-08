/*
 * Copyright (c) 2025. The NextFTC Team and other contributors.
 *
 * See the LICENSE in the root of this project for more information.
 */

package dev.nextftc.control.filters

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