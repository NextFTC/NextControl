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

import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * A vector in 2-dimensional space.
 * [T] represents the type of vector; a position cannot be used as a velocity.
 */
class Vector2d<T : Type>(val x: Distance, val y: Distance) {
    fun norm() = sqrt(x.value.pow(2.0) + y.value.pow(2.0))
    val length = norm()
    val angle = atan2(y.value, x.value)

    operator fun <O: Type> plus(other: Vector2d<O>): Vector2d<T> = Vector2d(x + other.x, y + other.y)
    operator fun <O: Type> minus(other: Vector2d<O>): Vector2d<T> = Vector2d(x - other.x, y - other.y)

    operator fun times(other: Distance):  Vector2d<T> = Vector2d(x * other, y * other)
    operator fun div(other: Distance): Vector2d<T> = Vector2d(x / other, y / other)

    operator fun unaryMinus(): Vector2d<T> = Vector2d(-x, -y)

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean = when (other) {
        is Vector2d<T> -> this.x == other.x && this.y == other.y
        else -> false
    }
}