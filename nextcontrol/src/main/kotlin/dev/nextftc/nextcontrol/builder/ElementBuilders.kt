package dev.nextftc.nextcontrol.builder

import dev.nextftc.nextcontrol.KineticState
import dev.nextftc.nextcontrol.feedback.FeedbackElement
import dev.nextftc.nextcontrol.feedback.PIDCoefficients
import dev.nextftc.nextcontrol.feedback.PIDElement
import dev.nextftc.nextcontrol.feedback.PIDType
import dev.nextftc.nextcontrol.filters.ChainedFilter
import dev.nextftc.nextcontrol.filters.Filter
import dev.nextftc.nextcontrol.filters.LowPassFilter

@DslMarker
annotation class ControlSystemMarker

enum class AngleType {
    RADIANS,
    DEGREES
}

class AngularFeedback(private val type: AngleType, private val feedbackElement: FeedbackElement) :
    FeedbackElement {

    override fun calculate(error: KineticState): Double = feedbackElement.calculate(error) * 2
}

@ControlSystemMarker
class FeedbackElementBuilder {

    var feedbackElement: FeedbackElement =
        FeedbackElement { 0.0 }

    fun posPid(coefficients: PIDCoefficients) = apply {
        feedbackElement = PIDElement(
            PIDType.POSITION,
            coefficients
        )
    }

    fun velPid(coefficients: PIDCoefficients) = apply {
        feedbackElement = PIDElement(
            PIDType.VELOCITY,
            coefficients
        )
    }

    fun custom(feedback: FeedbackElement) = apply {
        feedbackElement = feedback
    }

    fun angular(type: AngleType, factory: FeedbackElementBuilder.() -> Any) = apply {
        val builder = FeedbackElementBuilder()
        builder.factory()
        feedbackElement = AngularFeedback(type, builder.feedbackElement)
    }
}

@ControlSystemMarker
class FilterBuilder {

    private val filters: MutableList<Filter> = mutableListOf()

    @JvmOverloads
    fun lowPass(alpha: Double, startingEstimate: Double = 0.0) = apply {
        filters.add(LowPassFilter(alpha, startingEstimate))
    }

    fun custom(customFilter: Filter) = apply {
        filters.add(customFilter)
    }

    fun build(): Filter {
        return when (filters.size) {
            0 -> Filter { it }
            1 -> filters[0]
            else -> ChainedFilter(*filters.toTypedArray())
        }
    }
}