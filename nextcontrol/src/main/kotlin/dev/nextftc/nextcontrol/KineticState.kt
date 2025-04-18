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

package dev.nextftc.nextcontrol

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