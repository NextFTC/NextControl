package com.rowanmcalpin.nextftc.nextcontrol.feedback

import com.rowanmcalpin.nextftc.nextcontrol.Acceleration
import com.rowanmcalpin.nextftc.nextcontrol.KineticState
import com.rowanmcalpin.nextftc.nextcontrol.KineticType
import com.rowanmcalpin.nextftc.nextcontrol.Position
import com.rowanmcalpin.nextftc.nextcontrol.Velocity
import kotlin.reflect.KClass

class PIDController @JvmOverloads constructor(val kP: Double, val kI: Double = 0.0, val  kD: Double = 0.0) {
    var target = 0.0
    var sum = 0.0
    var lastError = 0.0
    var lastTimestamp = System.currentTimeMillis()

    fun calculate(referencePos: Double, referenceVel: Double?  = null): Double {
        val error =  target - referencePos
        val current = System.currentTimeMillis()
        val dt = current - lastTimestamp

        sum += 0.5 * (error + lastError) * dt

        val deriv = referenceVel ?: ((error - lastError) / dt)

        lastError = error
        lastTimestamp = current

        return kP * error + kD * deriv + kI * sum
    }
}

class PIDElement(val type: KineticType, val kP: Double, val kI: Double = 0.0, val kD: Double = 0.0): FeedbackElement {
    val controller = PIDController(kP, kI, kD)
    var target by controller::target

    override fun calculate(error: KineticState): Double = when (type) {
        is Position -> controller.calculate(error.position.value, error.velocity.value)
        is Velocity -> controller.calculate(error.velocity.value, error.acceleration.value)
        is Acceleration -> controller.calculate(error.acceleration.value, null)
    }
}