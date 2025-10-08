/*
 * Copyright (c) 2025. The NextFTC Team and other contributors.
 *
 * See the LICENSE in the root of this project for more information.
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