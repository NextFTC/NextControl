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
    private var feedback: FeedbackElement = NullFeedback(),
    private var feedforward: FeedforwardElement = NullFeedforward(),
    private var filter: FilterElement = FilterElement(),
    private var interpolator: InterpolatorElement,
) {
    constructor(startingInterpolator: InterpolatorElement) : this(interpolator = startingInterpolator)

    fun build() = ControlSystem(feedback, feedforward, filter, interpolator)

    fun setFeedback(feedback: FeedbackElement) = this.apply { this.feedback = feedback }

    fun setFeedforward(feedforward: FeedforwardElement) = this.apply { this.feedforward = feedforward }

    fun setFilter(filter: FilterElement) = this.apply { this.filter = filter }

    fun setInterpolator(interpolator: InterpolatorElement) = this.apply { this.interpolator = interpolator}

    fun setPosPID(coefficients: PIDCoefficients) =
        setFeedback(PIDElement(PIDType.POSITIONAL, coefficients))
    fun setPosPID(kP: Double, kI: Double, kD: Double) =
        setPosPID(PIDCoefficients(kP, kI, kD))

    fun setVelPID(coefficients: PIDCoefficients) =
        setFeedback(PIDElement(PIDType.VELOCITY, coefficients))
    fun setVelPID(kP: Double, kI: Double, kD: Double) =
        setVelPID(PIDCoefficients(kP, kI, kD))
}