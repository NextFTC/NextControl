package com.rowanmcalpin.nextftc.nextcontrol.feedback

import com.rowanmcalpin.nextftc.nextcontrol.utils.KineticState

/**
 * Traditional proportional-integral-derivative controller.
 * @author rbrott
 * @author j5155
 * @author zach.waffle
 */
open class PIDController @JvmOverloads constructor(
    var kP: Double, 
    var kI: Double = 0.0, 
    var kD: Double = 0.0
) {

    /**
     * Error computed in the last call to [calculate]
     */
    var lastError: Double = 0.0
        private set
    private var errorSum = 0.0
    private var lastTimestamp = 0L

    open fun calculate(
        timestamp: Long,
        posError: Double,
        velError: Double? = null
    ): Double {
        if (lastTimestamp == 0L) {
            lastError = posError
            lastTimestamp = timestamp
            return kP * posError + kD * (velError ?: 0.0)
        }

        val dt = (timestamp - lastTimestamp).toDouble()
        errorSum += (posError) * dt

        lastError = posError
        lastTimestamp = timestamp

        val derivError = velError ?: ((posError - lastError) / dt)

        return kP * posError + kI * errorSum + kD * derivError
    }

    fun calculate(posError: Double, velError: Double? = null) = calculate(System.nanoTime(), posError, velError)

    /**
     * Reset the controller's integral sum.
     */
    fun reset() {
        errorSum = 0.0
        lastError = 0.0
        lastTimestamp = 0
    }
}

/**
 * PID controller that operates on positions.
 * @param kP proportional gain
 * @param kI integral gain
 * @param kD derivative gain
 */
class PositionalPIDController @JvmOverloads constructor(
    kP: Double,
    kI: Double = 0.0,
    kD: Double = 0.0,
) :  PIDController(kP, kI, kD), FeedbackElement {
    fun calculate(timestamp: Long, error: KineticState) = this.calculate(timestamp, error.position, error.velocity)

    override fun calculate(error: KineticState) = this.calculate(error.position, error.velocity)
}

/**
 * PID controller that operates on velocities.
 * @param kP proportional gain
 * @param kI integral gain
 * @param kD derivative gain
 */
class VelocityPIDController @JvmOverloads constructor(
    kP: Double,
    kI: Double = 0.0,
    kD: Double = 0.0,
) : PIDController(kP, kI, kD), FeedbackElement {
    fun calculate(timestamp: Long, error: KineticState) = this.calculate(timestamp, error.velocity, error.acceleration)

    override fun calculate(error: KineticState) = this.calculate(error.velocity, error.acceleration)
}