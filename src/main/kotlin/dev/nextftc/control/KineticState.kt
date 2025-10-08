/*
 * Copyright (c) 2025. The NextFTC Team and other contributors.
 *
 * See the LICENSE in the root of this project for more information.
 */

package dev.nextftc.control

/**
 * Holds a state of a kinetic system.
 *
 * @param position the state's position
 * @param velocity the state's velocity
 * @param acceleration the state's acceleration
 *
 * @author BeepBot99
 */
data class KineticState @JvmOverloads constructor(
    val position: Double = 0.0,
    val velocity: Double = 0.0,
    val acceleration: Double = 0.0
) {

    operator fun minus(other: KineticState): KineticState = KineticState(
        position - other.position,
        velocity - other.velocity,
        acceleration - other.acceleration
    )

    operator fun times(scalar: Double): KineticState = KineticState(
        position * scalar,
        velocity * scalar,
        acceleration * scalar
    )

    operator fun plus(other: KineticState): KineticState = KineticState(
        position + other.position,
        velocity + other.velocity,
        acceleration + other.acceleration
    )
}