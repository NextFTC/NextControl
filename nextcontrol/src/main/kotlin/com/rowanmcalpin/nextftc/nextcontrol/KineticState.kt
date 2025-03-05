package com.rowanmcalpin.nextftc.nextcontrol

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
}