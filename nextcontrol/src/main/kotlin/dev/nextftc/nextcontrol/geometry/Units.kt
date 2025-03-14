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

@file:JvmName("Units")
package dev.nextftc.nextcontrol.geometry

import kotlin.math.abs

sealed class Type
class Position
class Velocity

/**
 * Represents a physical quantity
 * @param U the type of the quantity
 * @author BeepBot99
 */
abstract class Quantity<U: Quantity<U>> {
    /**
     * The value of the quantity
     */
    abstract val value: Double

    open operator fun plus(other: U): U = newInstance(value + other.value)
    operator fun minus(other: U): U = newInstance(value - other.value)
    operator fun times(other: U): U = newInstance(value * other.value)
    operator fun times(scalar: Double): U = newInstance(value * scalar)
    operator fun times(scalar: Int): U = newInstance(value * scalar)
    operator fun div(other: U): U = newInstance(value / other.value)
    operator fun div(scalar: Double): U = newInstance(value / scalar)
    operator fun div(scalar: Int): U = newInstance(value / scalar)
    operator fun unaryPlus(): U = newInstance(value)
    operator fun unaryMinus(): U = newInstance(-value)
    operator fun rem(other: U): U = newInstance(value % other.value)
    operator fun rem(divisor: Double): U = newInstance(value % divisor)
    operator fun rem(divisor: Int): U = newInstance(value % divisor)
    operator fun compareTo(other: U): Int = value.compareTo(other.value)

    val sign: Int get() = when {
        value > 0 -> 1
        value < 0 -> -1
        else -> 0
    }

    fun abs() = newInstance(abs(value))

    abstract override fun equals(other: Any?): Boolean
    abstract override fun hashCode(): Int
    abstract override fun toString(): String

    /**
     * @return if [value] is NaN
     */
    fun isNaN(): Boolean = value.isNaN()

    /**
     * Creates a new instance of the class with the given value
     * @param value the value to create an instance with
     */
    abstract fun newInstance(value: Double): U
}

fun <T: Quantity<T>> abs(quantity: T): T = quantity.abs()