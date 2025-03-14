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

@file:JvmName("Distances")
package dev.nextftc.nextcontrol.geometry

const val CENTIMETERS_TO_MILLIMETERS = 10.0
const val METERS_TO_MILLIMETERS = 1000.0
const val INCHES_TO_MILLIMETERS = 25.4
const val FEET_TO_MILLIMETERS = 304.8
const val YARDS_TO_MILLIMETERS = 914.4

/**
 * A quantity of distance or length
 * @param value the distance in millimeters
 * @author BeepBot99
 */
class Distance internal constructor(override val value: Double): Quantity<Distance>() {
    /**
     * The value of the distance in millimeters
     */
    @JvmField val inMm = value

    /**
     * The value of the distance in centimeters
     */
    @JvmField val inCm = value / CENTIMETERS_TO_MILLIMETERS

    /**
     * The value of the distance in meters
     */
    @JvmField val inMeters = value / METERS_TO_MILLIMETERS

    /**
     * The value of the distance in inches
     */
    @JvmField val inIn = value / INCHES_TO_MILLIMETERS

    /**
     * The value of the distance in feet
     */
    @JvmField val inFt = value / FEET_TO_MILLIMETERS

    /**
     * The value of the distance in yards
     */
    @JvmField val inYd = value / YARDS_TO_MILLIMETERS

    /**
     * Creates a new instance of [Distance] with the given value
     * @param value the value in millimeters to create an instance with
     */
    override fun newInstance(value: Double): Distance = Distance(value)

    override fun toString(): String = "$value mm"
    override fun equals(other: Any?): Boolean = when (other) {
        is Distance -> value == other.value
        else -> false
    }
    override fun hashCode(): Int = value.hashCode() + "mm".hashCode()
}

private fun fromUnit(value: Double, scalar: Double) = Distance(value * scalar)

/**
 * Creates a new [Distance] from a distance in millimeters
 * @param millimeters the distance in millimeters
 */
fun fromMm(millimeters: Double) = Distance(millimeters)

/**
 * Creates a new [Distance] from a distance in millimeters
 * @param millimeters the distance in millimeters
 */
fun fromMm(millimeters: Int) = fromMm(millimeters.toDouble())

/**
 * Creates a new [Distance] from a distance in centimeters
 * @param centimeters the distance in centimeters
 */
fun fromCm(centimeters: Double) = fromUnit(centimeters, CENTIMETERS_TO_MILLIMETERS)

/**
 * Creates a new [Distance] from a distance in centimeters
 * @param centimeters the distance in centimeters
 */
fun fromCm(centimeters: Int) = fromCm(centimeters.toDouble())

/**
 * Creates a new [Distance] from a distance in meters
 * @param meters the distance in meters
 */
fun fromMeters(meters: Double) = fromUnit(meters, METERS_TO_MILLIMETERS)

/**
 * Creates a new [Distance] from a distance in meters
 * @param meters the distance in meters
 */
fun fromMeters(meters: Int) = fromMeters(meters.toDouble())

/**
 * Creates a new [Distance] from a distance in inches
 * @param inches the distance in inches
 */
fun fromIn(inches: Double) = fromUnit(inches, INCHES_TO_MILLIMETERS)

/**
 * Creates a new [Distance] from a distance in inches
 * @param inches the distance in inches
 */
fun fromIn(inches: Int) = fromIn(inches.toDouble())

/**
 * Creates a new [Distance] from a distance in feet
 * @param feet the distance in feet
 */
fun fromFt(feet: Double) = fromUnit(feet, FEET_TO_MILLIMETERS)

/**
 * Creates a new [Distance] from a distance in feet
 * @param feet the distance in feet
 */
fun fromFt(feet: Int) = fromFt(feet.toDouble())

/**
 * Creates a new [Distance] from a distance in yards
 * @param yards the distance in yards
 */
fun fromYd(yards: Double) = fromUnit(yards, YARDS_TO_MILLIMETERS)

/**
 * Creates a new [Distance] from a distance in yards
 * @param yards the distance in yards
 */
fun fromYd(yards: Int) = fromYd(yards.toDouble())

/**
 * Creates a new [Distance] from a distance in millimeters
 */
val Double.mm: Distance get() = fromMm(this)

/**
 * Creates a new [Distance] from a distance in millimeters
 */
val Int.mm: Distance get() = fromMm(this)

/**
 * Creates a new [Distance] from a distance in centimeters
 */
val Double.cm: Distance get() = fromCm(this)

/**
 * Creates a new [Distance] from a distance in centimeters
 */
val Int.cm: Distance get() = fromCm(this)

/**
 * Creates a new [Distance] from a distance in meters
 */
val Double.m: Distance get() = fromMeters(this)

/**
 * Creates a new [Distance] from a distance in meters
 */
val Int.m: Distance get() = fromMeters(this)

/**
 * Creates a new [Distance] from a distance in inches
 */
val Double.inches: Distance get() = fromIn(this)

/**
 * Creates a new [Distance] from a distance in inches
 */
val Int.inches: Distance get() = fromIn(this)

/**
 * Creates a new [Distance] from a distance in inches
 */
val Double.inch: Distance get() = fromIn(this)

/**
 * Creates a new [Distance] from a distance in inches
 */
val Int.inch: Distance get() = fromIn(this)

/**
 * Creates a new [Distance] from a distance in feet
 */
val Double.ft: Distance get() = fromFt(this)

/**
 * Creates a new [Distance] from a distance in feet
 */
val Int.ft: Distance get() = fromFt(this)

/**
 * Creates a new [Distance] from a distance in yards
 */
val Double.yd: Distance get() = fromYd(this)

/**
 * Creates a new [Distance] from a distance in yards
 */
val Int.yd: Distance get() = fromYd(this)