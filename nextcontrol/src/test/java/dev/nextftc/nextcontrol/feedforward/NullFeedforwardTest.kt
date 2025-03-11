package dev.nextftc.nextcontrol.feedforward

import dev.nextftc.nextcontrol.utils.KineticState
import org.junit.Assert.*
import org.junit.Test

class NullFeedforwardTest {
    @Test
    fun `calculate returns 0 feedforward`() {
        // Arrange
        val feedforward = FeedforwardElement { 0.0 }
        val reference = KineticState(1.0, 2.0, 3.0)

        // Act
        val actual = feedforward.calculate(reference)

        // Assert
        assertEquals(0.0, actual, 0.0)
    }

    @Test
    fun `calculate returns 0 feedforward even for NaN and infinity input`() {
        // Arrange
        val feedforward = FeedforwardElement { 0.0 }
        val reference = KineticState(1.0, Double.POSITIVE_INFINITY, Double.NaN)

        // Act
        val actual = feedforward.calculate(reference)

        // Assert
        assertEquals(0.0, actual, 0.0)
    }
}