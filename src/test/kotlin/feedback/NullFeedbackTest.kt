/*
 * Copyright (c) 2025. The NextFTC Team and other contributors.
 *
 * See the LICENSE in the root of this project for more information.
 */

package dev.nextftc.nextcontrol.feedback

import dev.nextftc.control.KineticState
import dev.nextftc.control.feedback.FeedbackElement
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe

class NullFeedbackTest : AnnotationSpec() {
    @Test
    fun `calculate returns 0 feedback`() {
        // Arrange
        val feedback = FeedbackElement { 0.0 }
        val error = KineticState(1.0, 2.0, 3.0)

        // Act
        val actual = feedback.calculate(error)

        // Assert
        actual shouldBe 0.0
    }

    @Test
    fun `calculate returns 0 feedback even for NaN and infinity input`() {
        // Arrange
        val feedback = FeedbackElement { 0.0 }
        val error = KineticState(1.0, Double.POSITIVE_INFINITY, Double.NaN)

        // Act
        val actual = feedback.calculate(error)

        // Assert
        actual shouldBe 0.0
    }
}
