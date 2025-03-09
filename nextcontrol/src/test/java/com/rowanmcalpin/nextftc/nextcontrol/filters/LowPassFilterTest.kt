/*
 * NextFTC: a user-friendly control library for FIRST Tech Challenge
 * Copyright (C) 2025 Rowan McAlpin
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.rowanmcalpin.nextftc.nextcontrol.filters

import org.junit.Assert.*
import org.junit.Test

class LowPassFilterTest {

    @Test
    fun `exception thrown when alpha is less than 0 or greater than 1`() {
        // Arrange
        val tooLowAlpha = -1.0
        val tooHighAlpha = 2.0

        // Assert
        assertThrows(IllegalArgumentException::class.java) {
            LowPassFilter(tooLowAlpha)
        }
        assertThrows(IllegalArgumentException::class.java) {
            LowPassFilter(tooHighAlpha)
        }
    }
    @Test
    fun `returns sensor measurement when alpha is 0`() {
        // Arrange
        val lowPassFilter = LowPassFilter(0.0)
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
        val lowPassFilter = LowPassFilter(1.0, initialEstimate)
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
        val lowPassFilter = LowPassFilter(0.5)
        val nonZeroLowPassFilter = LowPassFilter(0.5, 10.0)

        // Assert
        assertEquals(0.0, lowPassFilter.previousEstimate, 0.0)
        assertEquals(10.0, nonZeroLowPassFilter.previousEstimate, 0.0)
    }

    @Test
    fun `estimates remains constant when sensor measurements are the same`() {
        // Arrange
        val lowPassFilter = LowPassFilter(0.5, 15.0)
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
        assertEquals(expectedFirstEstimate, firstEstimate, 0.0)
        assertEquals(expectedSecondEstimate, secondEstimate, 0.0)
        assertEquals(expectedThirdEstimate, thirdEstimate, 0.0)
    }
}