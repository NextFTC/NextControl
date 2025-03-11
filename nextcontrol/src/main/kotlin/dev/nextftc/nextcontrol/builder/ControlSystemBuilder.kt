package dev.nextftc.nextcontrol.builder

import dev.nextftc.nextcontrol.ControlSystem
import dev.nextftc.nextcontrol.feedback.FeedbackElement
import dev.nextftc.nextcontrol.feedback.PIDCoefficients
import dev.nextftc.nextcontrol.feedforward.FeedforwardElement
import dev.nextftc.nextcontrol.filters.FilterElement
import dev.nextftc.nextcontrol.interpolators.ConstantInterpolator
import dev.nextftc.nextcontrol.utils.KineticState

class ControlSystemBuilder {

    private val feedbackBuilder = FeedbackElementBuilder()
    private var feedforwardElement = FeedforwardElement { 0.0 }
    private var posFilterBuilder = FilterBuilder()
    private var velFilterBuilder = FilterBuilder()
    private var accelFilterBuilder = FilterBuilder()
    private var interpolator = ConstantInterpolator(KineticState())

    fun feedback(feedback: FeedbackElement) = apply { feedbackBuilder.custom(feedback) }
    fun posPid(coefficients: PIDCoefficients) = apply { feedbackBuilder.posPid(coefficients) }
    fun velPid(coefficients: PIDCoefficients) = apply { feedbackBuilder.velPid(coefficients) }
    fun angular(type: AngleType, factory: FeedbackElementBuilder.() -> Any) =
        apply { feedbackBuilder.angular(type, factory) }

    fun feedforward(feedforward: FeedforwardElement) = apply { feedforwardElement = feedforward }

    fun posFilter(factory: FilterBuilder.() -> Any) = apply { posFilterBuilder.factory() }

    fun velFilter(factory: FilterBuilder.() -> Any) = apply { velFilterBuilder.factory() }

    fun accelFilter(factory: FilterBuilder.() -> Any) = apply { accelFilterBuilder.factory() }
    fun build() = ControlSystem(
        feedbackBuilder.feedbackElement,
        feedforwardElement,
        FilterElement(
            posFilterBuilder.build(),
            velFilterBuilder.build(),
            accelFilterBuilder.build()
        ),
        interpolator
    )
}

fun controlSystem(init: ControlSystemBuilder.() -> Unit): ControlSystem {
    val builder = ControlSystemBuilder()
    builder.init()
    return builder.build()
}