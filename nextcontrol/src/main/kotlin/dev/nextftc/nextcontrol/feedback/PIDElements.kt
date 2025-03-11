package dev.nextftc.nextcontrol.feedback

import dev.nextftc.nextcontrol.utils.KineticState

/**
 * Coefficients for a PID controller.
 * @param kP proportional gain
 * @param kI integral gain
 * @param kD derivative gain
 */
data class PIDCoefficients @JvmOverloads constructor(val kP: Double, val kI: Double = 0.0, val kD: Double = 0.0)

enum class PIDType {
    POSITIONAL, VELOCITY
}

/**
 * Traditional proportional-integral-derivative controller.
 */
internal open class PIDController(var coefficients: PIDCoefficients) {
    @JvmOverloads constructor(kP: Double, kI: Double = 0.0, kD: Double = 0.0) :
            this(PIDCoefficients(kP, kI, kD))

    /**
     * Error computed in the last call to [calculate]
     */
    var lastError: Double = 0.0
        private set
    internal var errorSum = 0.0
    internal var lastTimestamp = 0L

    fun setPID(kP: Double, kI: Double, kD: Double) {
        coefficients = PIDCoefficients(kP, kI, kD)
    }

    open fun calculate(
        timestamp: Long,
        posError: Double,
        velError: Double? = null
    ): Double {
        if (lastTimestamp == 0L) {
            lastError = posError
            lastTimestamp = timestamp
            return coefficients.kP * posError
        }

        val dt = (timestamp - lastTimestamp).toDouble()
        errorSum += (posError) * dt

        lastError = posError
        lastTimestamp = timestamp

        val derivError = velError ?: ((posError - lastError) / dt)

        return coefficients.kP * posError + coefficients.kI * errorSum + coefficients.kD * derivError
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
 * @param coefficients PID gains
 */
class PIDElement(
    val type: PIDType,
    coefficients: PIDCoefficients
) : FeedbackElement {
    @JvmOverloads constructor(type: PIDType, kP: Double, kI: Double = 0.0, kD: Double = 0.0) :
            this(type, PIDCoefficients(kP, kI, kD))

    private val controller = PIDController(coefficients)
    var coefficients by controller::coefficients

    fun calculate(timestamp: Long, error: KineticState) = when (type) {
        PIDType.POSITIONAL -> controller.calculate(timestamp, error.position, error.velocity)
        PIDType.VELOCITY -> controller.calculate(timestamp, error.velocity, error.acceleration)
    }

    override fun calculate(error: KineticState) = this.calculate(System.nanoTime(), error)

    fun setPID(kP: Double, kI: Double, kD: Double) = controller.setPID(kP, kI, kD)
}