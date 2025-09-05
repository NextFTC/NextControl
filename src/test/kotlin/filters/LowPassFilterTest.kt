package dev.nextftc.nextcontrol.filters

import dev.nextftc.control.filters.LowPassFilter
import dev.nextftc.control.filters.LowPassParameters
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe


class LowPassFilterTest : AnnotationSpec() {

    @Test
    fun `parameters passed in constructor are put in parameters object`() {
        // Arrange
        val alpha = 0.5
        val initialEstimate = 10.0

        // Act
        val lowPassFilter = LowPassFilter(alpha, initialEstimate)
        val parameters = lowPassFilter.parameters

        // Assert
        parameters.alpha shouldBe alpha
        parameters.startingEstimate shouldBe initialEstimate
    }

    @Test
    fun `exception thrown when alpha is less than 0 or greater than 1`() {
        // Arrange
        val tooLowAlpha = -1.0
        val tooHighAlpha = 2.0

        val tooHighAlphaParameters = LowPassParameters(tooHighAlpha)
        val tooLowAlphaParameters = LowPassParameters(tooLowAlpha)

        // Assert
        shouldThrow<IllegalArgumentException> {
            LowPassFilter(tooLowAlphaParameters)
        }
        shouldThrow<IllegalArgumentException> {
            LowPassFilter(tooHighAlphaParameters)
        }
    }

    @Test
    fun `returns sensor measurement when alpha is 0`() {
        // Arrange
        val parameters = LowPassParameters(0.0)
        val lowPassFilter = LowPassFilter(parameters)
        val firstSensorMeasurement = 10.0
        val secondSensorMeasurement = 20.0

        // Act
        val firstEstimate = lowPassFilter.filter(firstSensorMeasurement)
        val secondEstimate = lowPassFilter.filter(secondSensorMeasurement)

        // Assert
        firstEstimate shouldBe firstSensorMeasurement
        secondEstimate shouldBe secondSensorMeasurement
    }

    @Test
    fun `returns initial estimate when alpha is 1`() {
        // Arrange
        val initialEstimate = 10.0
        val parameters = LowPassParameters(1.0, initialEstimate)
        val lowPassFilter = LowPassFilter(parameters)

        val firstSensorMeasurement = 15.0
        val secondSensorMeasurement = 20.0

        // Act
        val firstEstimate = lowPassFilter.filter(firstSensorMeasurement)
        val secondEstimate = lowPassFilter.filter(secondSensorMeasurement)

        // Assert
        firstEstimate shouldBe initialEstimate
        secondEstimate shouldBe initialEstimate
    }

    @Test
    fun `default initial estimate is 0`() {
        // Arrange
        val parameters = LowPassParameters(0.5)
        val lowPassFilter = LowPassFilter(parameters)

        val nonZeroParameters = LowPassParameters(0.5, 10.0)
        val nonZeroLowPassFilter = LowPassFilter(nonZeroParameters)

        // Assert
        lowPassFilter.previousEstimate shouldBe 0.0
        nonZeroLowPassFilter.previousEstimate shouldBe 10.0
    }

    @Test
    fun `estimates remains constant when sensor measurements are the same`() {
        // Arrange
        val parameters = LowPassParameters(0.5, 15.0)
        val lowPassFilter = LowPassFilter(parameters)
        val firstSensorMeasurement = 15.0
        val secondSensorMeasurement = 15.0
        val expectedEstimate = 15.0

        // Act
        val firstEstimate = lowPassFilter.filter(firstSensorMeasurement)
        val secondEstimate = lowPassFilter.filter(secondSensorMeasurement)

        // Assert
        firstEstimate shouldBe expectedEstimate
        secondEstimate shouldBe expectedEstimate
    }

    @Test
    fun `applies low pass filter and smooths input correctly`() {
        // Arrange
        val lowPassFilter = LowPassFilter(0.5)
        val firstSensorMeasurement = 10.0
        val secondSenorMeasurement = 20.0
        val thirdSensorMeasurement = 25.0
        val expectedFirstEstimate = 5.0
        val expectedSecondEstimate = 12.5
        val expectedThirdEstimate = 18.75

        // Act
        val firstEstimate = lowPassFilter.filter(firstSensorMeasurement)
        val secondEstimate = lowPassFilter.filter(secondSenorMeasurement)
        val thirdEstimate = lowPassFilter.filter(thirdSensorMeasurement)

        // Assert
        firstEstimate shouldBe expectedFirstEstimate
        secondEstimate shouldBe expectedSecondEstimate
        thirdEstimate shouldBe expectedThirdEstimate
    }

    @Test
    fun `changing alpha halfway through changes filter behavior`() {
        // Arrange
        val parameters = LowPassParameters(0.0)
        val filter = LowPassFilter(parameters)

        val firstSensorMeasurement = 10.0
        val expectedFirstEstimate = 10.0
        val secondSensorMeasurement = 15.0
        val expectedSecondEstimate = 15.0
        val secondAlpha = 0.5
        val thirdSensorMeasurement = 20.0
        val expectedThirdEstimate = 17.5
        val thirdAlpha = 1.0
        val fourthSensorMeasurement = 25.0
        val expectedFourthEstimate = 17.5

        // Act
        val firstEstimate = filter.filter(firstSensorMeasurement)
        val secondEstimate = filter.filter(secondSensorMeasurement)
        parameters.alpha = secondAlpha
        val thirdEstimate = filter.filter(thirdSensorMeasurement)
        parameters.alpha = thirdAlpha
        val fourthEstimate = filter.filter(fourthSensorMeasurement)

        // Assert
        firstEstimate shouldBe expectedFirstEstimate
        secondEstimate shouldBe expectedSecondEstimate
        thirdEstimate shouldBe expectedThirdEstimate
        fourthEstimate shouldBe expectedFourthEstimate
    }
}