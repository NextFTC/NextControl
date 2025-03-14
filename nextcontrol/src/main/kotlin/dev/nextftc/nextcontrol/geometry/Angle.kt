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

@file:JvmName("Angles")
package dev.nextftc.nextcontrol.geometry

const val DEGREES_TO_RADIANS = Math.PI / 180
const val REVOLUTIONS_TO_RADIANS = 2 * Math.PI

/**
 * An angle
 * @param value the angle in radians
 * @author BeepBot99
 */
class Angle internal constructor(override val value: Double): Quantity<Angle>() {
    /**
     * The value of the angle in radians
     */
    @JvmField val inRad = value

    /**
     * The value of the angle in degrees
     */
    @JvmField val inDeg = value / DEGREES_TO_RADIANS

    /**
     * The value of the angle in full revolutions
     */
    @JvmField val inRev = value / REVOLUTIONS_TO_RADIANS

    /**
     * A new [Angle] wrapped from 0 to 2pi
     */
    @get:JvmName("wrapped")
    val wrapped get() = Angle(wrapAngle0To2Pi(value))

    /**
     * A new [Angle] wrapped from -pi to pi
     */
    @get:JvmName("normalized")
    val normalized get() = Angle(wrapAnglePiToPi(value))

    override fun newInstance(value: Double): Angle = Angle(value).normalized

    override fun toString(): String = "$value rad"
    override fun equals(other: Any?): Boolean = when (other) {
        is Angle  -> value == other.value
        else -> false
    }
    override fun hashCode(): Int = value.hashCode() + "rad".hashCode()
}

private fun fromUnit(value: Double, scalar: Double) = Angle(value * scalar).normalized

/**
 * Creates a new [Angle] from an angle in radians
 * @param radians the angle in radians
 */
fun fromRad(radians: Double) = fromUnit(radians, 1.0)

/**
 * Creates a new [Angle] from an angle in radians
 * @param radians the angle in radians
 */
fun fromRad(radians: Int) = fromRad(radians.toDouble())

/**
 * Creates a new [Angle] from an angle in degrees
 * @param degrees the angle in degrees
 */
fun fromDeg(degrees: Double) = fromUnit(degrees, DEGREES_TO_RADIANS)

/**
 * Creates a new [Angle] from an angle in degrees
 * @param degrees the angle in degrees
 */
fun fromDeg(degrees: Int) = fromDeg(degrees.toDouble())

/**
 * Creates a new [Angle] from an angle in full revolutions
 * @param revolutions the angle in full revolutions
 */
fun fromRev(revolutions: Double) = fromUnit(revolutions, REVOLUTIONS_TO_RADIANS)

/**
 * Creates a new [Angle] from an angle in full revolutions
 * @param revolutions the angle in full revolutions
 */
fun fromRev(revolutions: Int) = fromRev(revolutions.toDouble())

fun wrapAngle0To2Pi(angle: Double) = ((angle % (2 * Math.PI)) + 2 * Math.PI) % (2 * Math.PI)
fun wrapAnglePiToPi(angle: Double) = ((angle + Math.PI) % (2 * Math.PI) + 2 * Math.PI) % (2 * Math.PI) - Math.PI

/**
 * Creates a new [Angle] from an angle in radians
 */
val Double.rad: Angle get() = fromRad(this)

/**
 * Creates a new [Angle] from an angle in radians
 */
val Int.rad: Angle get() = fromRad(this)

/**
 * Creates a new [Angle] from an angle in degrees
 */
val Double.deg: Angle get() = fromDeg(this)

/**
 * Creates a new [Angle] from an angle in degrees
 */
val Int.deg: Angle get() = fromDeg(this)

/**
 * Creates a new [Angle] from an angle in full revolutions
 */
val Double.rev: Angle get() = fromRev(this)

/**
 * Creates a new [Angle] from an angle in full revolutions
 */
val Int.rev: Angle get() = fromRev(this)