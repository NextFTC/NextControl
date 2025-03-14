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

package dev.nextftc.nextcontrol.geometry

/**
 * A pose in 2-dimensional space.
 * This can represent either a Position or a Velocity.
 */
class Pose2d<T : Type>(val vector: Vector2d<T>, val heading: Angle) : Type() {
    constructor(x: Distance, y: Distance,  heading: Angle) : this(Vector2d(x, y), heading)

    operator fun <O : Type> plus(other: Pose2d<O>): Pose2d<T> = Pose2d(
            this.vector + other.vector,
            this.heading + other.heading
        )

    operator fun minus(other: Pose2d<T>): Twist2d<T> = Twist2d(
        this.vector - other.vector,
        this.heading - other.heading
    )
}

/**
 * The difference between two poses.
 */
class Twist2d<T : Type>(val vector: Vector2d<T>, val heading: Angle) : Type() {

}