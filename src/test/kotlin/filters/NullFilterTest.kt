/*
 * Copyright (c) 2025. The NextFTC Team and other contributors.
 *
 * See the LICENSE in the root of this project for more information.
 */

package dev.nextftc.nextcontrol.filters

import dev.nextftc.control.filters.Filter
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe

class NullFilterTest : AnnotationSpec() {

    @Test
    fun `returns value passed in`() {
        // Arrange
        val filter = Filter { it }

        val input = 10.0

        // Act
        val actual = filter.filter(input)

        // Assert
        actual shouldBe input
    }
}