package dev.nextftc.nextcontrol.builder

import dev.nextftc.nextcontrol.feedback.*
import dev.nextftc.nextcontrol.filters.ChainedFilter
import dev.nextftc.nextcontrol.filters.Filter
import dev.nextftc.nextcontrol.filters.LowPassFilter
import dev.nextftc.nextcontrol.filters.LowPassParameters

@DslMarker
annotation class ControlSystemMarker

@ControlSystemMarker
class FeedbackElementBuilder {

    var feedbackElement: FeedbackElement =
        FeedbackElement { 0.0 }

    fun custom(feedback: FeedbackElement) =
        apply { feedbackElement = feedback }

    fun posPid(coefficients: PIDCoefficients) =
        custom(PIDElement(PIDType.POSITION, coefficients))

    @JvmOverloads
    fun posPid(kP: Double, kI: Double = 0.0, kD: Double = 0.0) =
        posPid(PIDCoefficients(kP, kI, kD))

    fun velPid(coefficients: PIDCoefficients) =
        custom(PIDElement(PIDType.VELOCITY, coefficients))

    @JvmOverloads
    fun velPid(kP: Double, kI: Double = 0.0, kD: Double = 0.0) =
        velPid(PIDCoefficients(kP, kI, kD))

    fun posSquID(coefficients: PIDCoefficients) =
        custom(SquIDElement(PIDType.POSITION, coefficients))

    @JvmOverloads
    fun posSquID(kP: Double, kI: Double = 0.0, kD: Double = 0.0) =
        posSquID(PIDCoefficients(kP, kI, kD))

    fun velSquID(coefficients: PIDCoefficients) =
        custom(SquIDElement(PIDType.VELOCITY, coefficients))

    @JvmOverloads
    fun velSquID(kP: Double, kI: Double = 0.0, kD: Double = 0.0) =
        velSquID(PIDCoefficients(kP, kI, kD))


    fun angular(type: AngleType, factory: FeedbackElementBuilder.() -> Any) = apply {
        val builder = FeedbackElementBuilder()
        builder.factory()
        feedbackElement = AngularFeedback(type, builder.feedbackElement)
    }
}

@ControlSystemMarker
class FilterBuilder {

    fun custom(customFilter: Filter) = apply {
        filters.add(customFilter)
    }

    private val filters: MutableList<Filter> = mutableListOf()

    fun lowPass(parameters: LowPassParameters) = custom(LowPassFilter(parameters))

    @JvmOverloads
    fun lowPass(alpha: Double, startingEstimate: Double = 0.0) = lowPass(
        LowPassParameters(
            alpha,
            startingEstimate
        )
    )

    fun build(): Filter {
        return when (filters.size) {
            0 -> Filter { it }
            1 -> filters[0]
            else -> ChainedFilter(*filters.toTypedArray())
        }
    }
}