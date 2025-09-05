package dev.nextftc.control.builder

import dev.nextftc.control.feedback.*
import dev.nextftc.control.filters.ChainedFilter
import dev.nextftc.control.filters.Filter
import dev.nextftc.control.filters.LowPassFilter
import dev.nextftc.control.filters.LowPassParameters
import dev.nextftc.functionalInterfaces.Configurator

@DslMarker
annotation class ControlSystemMarker

@ControlSystemMarker
class FeedbackElementBuilder {

    var feedbackElement: FeedbackElement =
        FeedbackElement { 0.0 }

    fun custom(feedback: FeedbackElement) =
        apply { feedbackElement = feedback }

    fun posPid(coefficients: PIDCoefficients) =
        custom(PIDElement(FeedbackType.POSITION, coefficients))

    @JvmOverloads
    fun posPid(kP: Double, kI: Double = 0.0, kD: Double = 0.0) =
        posPid(PIDCoefficients(kP, kI, kD))

    fun velPid(coefficients: PIDCoefficients) =
        custom(PIDElement(FeedbackType.VELOCITY, coefficients))

    @JvmOverloads
    fun velPid(kP: Double, kI: Double = 0.0, kD: Double = 0.0) =
        velPid(PIDCoefficients(kP, kI, kD))

    fun posSquID(coefficients: PIDCoefficients) =
        custom(SquIDElement(FeedbackType.POSITION, coefficients))

    @JvmOverloads
    fun posSquID(kP: Double, kI: Double = 0.0, kD: Double = 0.0) =
        posSquID(PIDCoefficients(kP, kI, kD))

    fun velSquID(coefficients: PIDCoefficients) =
        custom(SquIDElement(FeedbackType.VELOCITY, coefficients))

    @JvmOverloads
    fun velSquID(kP: Double, kI: Double = 0.0, kD: Double = 0.0) =
        velSquID(PIDCoefficients(kP, kI, kD))


    fun angular(type: AngleType, configurator: Configurator<FeedbackElementBuilder>) = apply {
        val builder = FeedbackElementBuilder()
        configurator.configure(builder)
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