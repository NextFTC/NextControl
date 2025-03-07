package com.rowanmcalpin.nextftc.nextcontrol.feedback

import com.rowanmcalpin.nextftc.nextcontrol.utils.KineticState

/**
 * PID controller with various feedforward components.
 * Originally from Roadrunner 0.5
 * Ported to Kotlin by Zach.Waffle and j5155
 */
open class PIDController @JvmOverloads constructor(
    var kP: Double, 
    var kI: Double = 0.0, 
    var kD: Double = 0.0
) {
    /**
     * Target position (that is, the controller setpoint).
     */
    var targetPosition: Int = 0

    /**
     * Target velocity.
     */
    var targetVelocity: Double = 0.0

    /**
     * Error computed in the last call to [calculate]
     */
    var lastError: Double = 0.0
    private var errorSum = 0.0
    private var lastTimestamp = 0L

    open fun calculate(
        timestamp: Long,
        error: KineticState
    ): Double {
        val dt = (timestamp - lastTimestamp).toDouble()
        errorSum += 0.5 * (error.position) * dt

        lastError = error.position
        lastTimestamp = timestamp

        return kP * error.position + kI * errorSum + kD * error.velocity
    }

    open fun calculate(error: KineticState): Double = calculate(System.nanoTime(), error)

    /**
     * Run a single iteration of the controller.
     *
     * @param timestamp        measurement timestamp as given by [System.nanoTime]
     * @param measuredPosition measured position (feedback)
     * @param measuredVelocity measured velocity
     */
    @JvmOverloads
    open fun calculate(
        timestamp: Long,
        measuredPosition: Double,
        measuredVelocity: Double? = null
    ): Double {
        val error = targetPosition - measuredPosition

        val dt = (timestamp - lastTimestamp).toDouble()
        errorSum += 0.5 * (error + lastError) * dt
        val errorDeriv = if (dt <= 0.0) 0.0 else (error - lastError) / dt

        val velError = if (measuredVelocity == null) {
            errorDeriv
        } else {
            targetVelocity - measuredVelocity
        }

        return calculate(timestamp, KineticState(error, velError))
    }

    fun calculate(
        measuredPosition: Double
    ): Double {
        return calculate(System.nanoTime(), measuredPosition)
    }

    /**
     * Reset the controller's integral sum.
     */
    fun reset() {
        errorSum = 0.0
        lastError = 0.0
        lastTimestamp = 0
    }
}

class PIDElement(
    kP: Double,
    kI: Double = 0.0,
    kD: Double = 0.0
) : FeedbackElement {
    private val controller = PIDController(kP, kI, kD)

    override fun calculate(error: KineticState): Double {
        return controller.calculate(error)
    }
}