package com.rowanmcalpin.nextftc.nextcontrol

import kotlin.test.Test
import kotlin.test.assertTrue

fun KineticState.Companion.of(pos: Double, vel: Double, acc: Double) = KineticState(Position(pos), Velocity(vel), Acceleration(acc))

class KineticTypeTest {
    @Test
    fun `kinetic type subtractions are correct`() {
        val state1 = KineticState.of(0.0, 0.0, 0.0)
        val state2 = KineticState.of(0.0, 0.0, 0.0)

        val error = state1 - state2

        assertTrue(error.position is Position)
        assertTrue(error.velocity is Velocity)
        assertTrue(error.acceleration is Acceleration)
    }
}