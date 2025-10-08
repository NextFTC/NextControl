/*
 * Copyright (c) 2025. The NextFTC Team and other contributors.
 *
 * See the LICENSE in the root of this project for more information.
 */

package dev.nextftc.nextcontrol.feedforward

import dev.nextftc.control.KineticState
import dev.nextftc.control.feedforward.FeedforwardElement
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe

class NullFeedforwardTest : AnnotationSpec() {

    @Test
    fun `calculate returns 0 feedforward`() {
        // Arrange
        val feedforward = FeedforwardElement { 0.0 }
        val reference = KineticState(1.0, 2.0, 3.0)

        // Act
        val actual = feedforward.calculate(reference)

        // Assert
        actual shouldBe 0.0
    }

    @Test
    fun `calculate returns 0 feedforward even for NaN and infinity input`() {
        // Arrange
        val feedforward = FeedforwardElement { 0.0 }
        val reference = KineticState(
            Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NaN
        )

        // Act
        val actual = feedforward.calculate(reference)

        // Assert
        actual shouldBe 0.0
    }
}