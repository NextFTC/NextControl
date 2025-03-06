package com.rowanmcalpin.nextftc.nextcontrol.feedback

import com.rowanmcalpin.nextftc.nextcontrol.Acceleration
import com.rowanmcalpin.nextftc.nextcontrol.Position
import com.rowanmcalpin.nextftc.nextcontrol.Velocity
import org.junit.Assert.assertTrue
import org.junit.Test

class PIDFeedbackTest {
    @Test
    fun `pid can be constructed on any kinetictype`() {
        val pos = PIDElement(Position.TYPE, 0.1)
        val vel = PIDElement(Velocity.TYPE, 0.1)
        val acc = PIDElement(Acceleration.TYPE, 0.1)

        assertTrue(pos.type is Position)
        assertTrue(vel.type is Velocity)
        assertTrue(acc.type is Acceleration)
    }
}