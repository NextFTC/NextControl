package dev.nextftc.nextcontrol.builder

import dev.nextftc.nextcontrol.ControlSystem
import dev.nextftc.nextcontrol.feedback.AngleType
import dev.nextftc.nextcontrol.feedback.FeedbackElement
import dev.nextftc.nextcontrol.feedback.PIDCoefficients
import dev.nextftc.nextcontrol.feedforward.*
import dev.nextftc.nextcontrol.filters.FilterElement
import dev.nextftc.nextcontrol.interpolators.ConstantInterpolator
import dev.nextftc.nextcontrol.interpolators.FirstOrderEMAInterpolator
import dev.nextftc.nextcontrol.interpolators.FirstOrderEMAParameters
import dev.nextftc.nextcontrol.interpolators.InterpolatorElement
import dev.nextftc.nextcontrol.utils.KineticState

class ControlSystemBuilder {

    private val feedbackBuilder = FeedbackElementBuilder()
    private var feedforward = FeedforwardElement { 0.0 }
    private var posFilterBuilder = FilterBuilder()
    private var velFilterBuilder = FilterBuilder()
    private var accelFilterBuilder = FilterBuilder()
    private var interpolator: InterpolatorElement = ConstantInterpolator(KineticState())

    fun feedback(feedback: FeedbackElement) =
        apply { feedbackBuilder.custom(feedback) }

    fun posPid(coefficients: PIDCoefficients) =
        apply { feedbackBuilder.posPid(coefficients) }

    @JvmOverloads
    fun posPid(kP: Double, kI: Double = 0.0, kD: Double = 0.0) =
        apply { feedbackBuilder.posPid(kP, kI, kD) }

    fun velPid(coefficients: PIDCoefficients) =
        apply { feedbackBuilder.velPid(coefficients) }

    @JvmOverloads
    fun velPid(kP: Double, kI: Double = 0.0, kD: Double = 0.0) =
        apply { feedbackBuilder.velPid(kP, kI, kD) }

    fun posSquID(coefficients: PIDCoefficients) =
        apply { feedbackBuilder.posSquID(coefficients) }

    @JvmOverloads
    fun posSquid(kP: Double, kI: Double = 0.0, kD: Double = 0.0) =
        apply { feedbackBuilder.posSquID(kP, kI, kD) }

    fun velSquID(coefficients: PIDCoefficients) =
        apply { feedbackBuilder.velSquID(coefficients) }

    @JvmOverloads
    fun velSquID(kP: Double, kI: Double = 0.0, kD: Double = 0.0) =
        apply { feedbackBuilder.velSquID(kP, kI, kD) }

    fun angular(type: AngleType, factory: FeedbackElementBuilder.() -> Any) =
        apply { feedbackBuilder.angular(type, factory) }

    fun feedforward(feedforward: FeedforwardElement) =
        apply { this.feedforward = feedforward }

    fun basicFF(parameters: BasicFeedforwardParameters) =
        feedforward(BasicFeedforward(parameters))

    @JvmOverloads
    fun basicFF(kV: Double = 0.0, kA: Double = 0.0, kS: Double = 0.0) =
        basicFF(BasicFeedforwardParameters(kV, kA, kS))

    fun elevatorFF(parameters: GravityFeedforwardParameters) =
        feedforward(ElevatorFeedforward(parameters))

    @JvmOverloads
    fun elevatorFF(kG: Double = 0.0, kV: Double = 0.0, kA: Double = 0.0, kS: Double = 0.0) =
        elevatorFF(GravityFeedforwardParameters(kG, kV, kA, kS))

    fun armFF(parameters: GravityFeedforwardParameters) =
        feedforward(ArmFeedforward(parameters))

    @JvmOverloads
    fun armFF(kG: Double = 0.0, kV: Double = 0.0, kA: Double = 0.0, kS: Double = 0.0) =
        armFF(GravityFeedforwardParameters(kG, kV, kA, kS))

    fun posFilter(factory: FilterBuilder.() -> Any) =
        apply { posFilterBuilder.factory() }

    fun velFilter(factory: FilterBuilder.() -> Any) =
        apply { velFilterBuilder.factory() }

    fun accelFilter(factory: FilterBuilder.() -> Any) =
        apply { accelFilterBuilder.factory() }

    fun interpolator(interpolator: InterpolatorElement) =
        apply { this.interpolator = interpolator }

    fun emaInterpolator(parameters: FirstOrderEMAParameters) =
        interpolator(FirstOrderEMAInterpolator(parameters))

    fun build() = ControlSystem(
        feedbackBuilder.feedbackElement, feedforward, FilterElement(
            posFilterBuilder.build(), velFilterBuilder.build(), accelFilterBuilder.build()
        ), interpolator
    )
}

fun controlSystem(init: ControlSystemBuilder.() -> Unit): ControlSystem {
    val builder = ControlSystemBuilder()
    builder.init()
    return builder.build()
}