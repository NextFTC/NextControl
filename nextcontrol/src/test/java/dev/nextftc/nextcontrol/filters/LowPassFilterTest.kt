package dev.nextftc.nextcontrol.filters

import kotlin.test.assertEquals
import kotlin.test.Test
import kotlin.test.assertFailsWith

class LowPassFilterTest {
    @Test
    fun `parameters passed in constructor are put in parameters object`() {
        // Arrange
        val alpha = 0.5
        val initialEstimate = 10.0

        // Act
        val lowPassFilter = dev.nextftc.nextcontrol.filters.LowPassFilter(alpha, initialEstimate)
        val parameters = lowPassFilter.parameters

        // Assert
        assertEquals(alpha, parameters.alpha, 0.0)
        assertEquals(initialEstimate, parameters.startingEstimate, 0.0)
    }

    @Test
    fun `exception thrown when alpha is less than 0 or greater than 1`() {
        // Arrange
        val tooLowAlpha = -1.0
        val tooHighAlpha = 2.0

        val tooHighAlphaParameters = dev.nextftc.nextcontrol.filters.LowPassParameters(tooHighAlpha)
        val tooLowAlphaParameters = dev.nextftc.nextcontrol.filters.LowPassParameters(tooLowAlpha)

        // Assert
        assertFailsWith<IllegalArgumentException> {
            LowPassFilter(tooLowAlpha)
        }
        assertFailsWith<IllegalArgumentException> {
            LowPassFilter(tooHighAlpha)
        }
    }
    @Test
    fun `returns sensor measurement when alpha is 0`() {
        // Arrange
        val parameters = dev.nextftc.nextcontrol.filters.LowPassParameters(0.0)
        val lowPassFilter = dev.nextftc.nextcontrol.filters.LowPassFilter(parameters)
        val firstSensorMeasurement = 10.0
        val secondSensorMeasurement = 20.0

        // Act
        val firstEstimate = lowPassFilter.filter(firstSensorMeasurement)
        val secondEstimate = lowPassFilter.filter(secondSensorMeasurement)

        // Assert
        assertEquals(firstSensorMeasurement, firstEstimate, 0.0)
        assertEquals(secondSensorMeasurement, secondEstimate, 0.0)
    }

    @Test
    fun `returns initial estimate when alpha is 1`() {
        // Arrange
        val initialEstimate = 10.0
        val parameters = dev.nextftc.nextcontrol.filters.LowPassParameters(1.0, initialEstimate)
        val lowPassFilter = dev.nextftc.nextcontrol.filters.LowPassFilter(parameters)

        val firstSensorMeasurement = 15.0
        val secondSensorMeasurement = 20.0

        // Act
        val firstEstimate = lowPassFilter.filter(firstSensorMeasurement)
        val secondEstimate = lowPassFilter.filter(secondSensorMeasurement)

        // Assert
        assertEquals(initialEstimate, firstEstimate, 0.0)
        assertEquals(initialEstimate, secondEstimate, 0.0)
    }

    @Test
    fun `default initial estimate is 0`() {
        // Arrange
        val parameters = dev.nextftc.nextcontrol.filters.LowPassParameters(0.5)
        val lowPassFilter = dev.nextftc.nextcontrol.filters.LowPassFilter(parameters)

        val nonZeroParameters = dev.nextftc.nextcontrol.filters.LowPassParameters(0.5, 10.0)
        val nonZeroLowPassFilter = dev.nextftc.nextcontrol.filters.LowPassFilter(nonZeroParameters)

        // Assert
        assertEquals(0.0, lowPassFilter.previousEstimate, 0.0)
        assertEquals(10.0, nonZeroLowPassFilter.previousEstimate, 0.0)
    }

    @Test
    fun `estimates remains constant when sensor measurements are the same`() {
        // Arrange
        val parameters = dev.nextftc.nextcontrol.filters.LowPassParameters(0.5, 15.0)
        val lowPassFilter = dev.nextftc.nextcontrol.filters.LowPassFilter(parameters)
        val firstSensorMeasurement = 15.0
        val secondSensorMeasurement = 15.0
        val expectedEstimate = 15.0

        // Act
        val firstEstimate = lowPassFilter.filter(firstSensorMeasurement)
        val secondEstimate = lowPassFilter.filter(secondSensorMeasurement)

        // Assert
        assertEquals(expectedEstimate, firstEstimate, 0.0)
        assertEquals(expectedEstimate, secondEstimate, 0.0)
    }

    @Test
    fun `applies low pass filter and smooths input correctly`() {
        // Arrange
        val lowPassFilter = dev.nextftc.nextcontrol.filters.LowPassFilter(0.5)
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
        assertEquals(expectedFirstEstimate, firstEstimate, 0.0)
        assertEquals(expectedSecondEstimate, secondEstimate, 0.0)
        assertEquals(expectedThirdEstimate, thirdEstimate, 0.0)
    }

    @Test
    fun `changing alpha halfway through changes filter behavior`() {
        // Arrange
        val parameters = dev.nextftc.nextcontrol.filters.LowPassParameters(0.0)
        val filter = dev.nextftc.nextcontrol.filters.LowPassFilter(parameters)

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
        assertEquals(expectedFirstEstimate, firstEstimate, 0.0)
        assertEquals(expectedSecondEstimate, secondEstimate, 0.0)
        assertEquals(expectedThirdEstimate, thirdEstimate, 0.0)
        assertEquals(expectedFourthEstimate, fourthEstimate, 0.0)
    }
}