package com.rowanmcalpin.nextftc.nextcontrol

import com.rowanmcalpin.nextftc.nextcontrol.feedback.FeedbackElement
import com.rowanmcalpin.nextftc.nextcontrol.feedback.NullFeedback
import com.rowanmcalpin.nextftc.nextcontrol.feedback.PIDCoefficients
import com.rowanmcalpin.nextftc.nextcontrol.feedback.PIDElement
import com.rowanmcalpin.nextftc.nextcontrol.feedback.PIDType
import com.rowanmcalpin.nextftc.nextcontrol.feedforward.FeedforwardElement
import com.rowanmcalpin.nextftc.nextcontrol.feedforward.NullFeedforward
import com.rowanmcalpin.nextftc.nextcontrol.filters.FilterElement
import com.rowanmcalpin.nextftc.nextcontrol.interpolators.InterpolatorElement

class ControlSystemBuilder private constructor(
    private val feedback: FeedbackElement = NullFeedback(),
    private val feedforward: FeedforwardElement = NullFeedforward(),
    private val filter: FilterElement = FilterElement(),
    private val interpolator: InterpolatorElement,
) {
    private constructor(
        system: ControlSystemBuilder,
        feedback: FeedbackElement = system.feedback,
        feedforward: FeedforwardElement = system.feedforward,
        filter: FilterElement = system.filter,
        interpolator: InterpolatorElement = system.interpolator,
    ) : this(feedback, feedforward, filter, interpolator)

    constructor(startingInterpolator: InterpolatorElement) : this(interpolator = startingInterpolator)

    fun build() = ControlSystem(feedback, feedforward, filter, interpolator)

    fun withFeedback(feedback: FeedbackElement) = ControlSystemBuilder(
        this,
        feedback = feedback
    )

    fun withFeedforward(feedforward: FeedforwardElement) = ControlSystemBuilder(
        this,
        feedforward = feedforward
    )


    fun withFilter(filter: FilterElement) = ControlSystemBuilder(
        this,
        filter = filter
    )

    fun withInterpolator(interpolator: InterpolatorElement) = ControlSystemBuilder(
        this,
        interpolator = interpolator
    )

    fun withPosPID(coefficients: PIDCoefficients) =
        withFeedback(PIDElement(PIDType.POSITIONAL, coefficients))
    fun withPosPID(kP: Double, kI: Double, kD: Double) =
        withPosPID(PIDCoefficients(kP, kI, kD))

    fun withVelPID(coefficients: PIDCoefficients) =
        withFeedback(PIDElement(PIDType.VELOCITY, coefficients))
    fun withVelPID(kP: Double, kI: Double, kD: Double) =
        withVelPID(PIDCoefficients(kP, kI, kD))
}