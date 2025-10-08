/*
 * Copyright (c) 2025. The NextFTC Team and other contributors.
 *
 * See the LICENSE in the root of this project for more information.
 */

package dev.nextftc.nextcontrol.filters

import dev.nextftc.control.KineticState
import dev.nextftc.control.filters.Filter
import dev.nextftc.control.filters.FilterElement
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class FilterElementTest : AnnotationSpec() {

    @Test
    fun `uses PassThroughFilter by default`() {
        // Arrange
        val filterElement = FilterElement()
        val input = KineticState(10.0, 20.0, 30.0)

        // Act
        val actual = filterElement.filter(input)

        // Assert
        actual shouldBe input
    }

    @Test
    fun `uses custom position filter`() {
        // Arrange
        val positionFilter = mockk<Filter>()
        val filterElement = FilterElement(positionFilter = positionFilter)

        val input = KineticState(10.0, 20.0, 30.0)
        val expected = KineticState(1.0, 20.0, 30.0)

        every { positionFilter.filter(10.0) } returns 1.0

        // Act
        val actual = filterElement.filter(input)

        // Assert
        actual shouldBe expected
        verify(exactly = 1) { positionFilter.filter(10.0) }
    }

    @Test
    fun `uses custom velocity filter`() {
        // Arrange
        val velocityFilter = mockk<Filter>()
        val filterElement = FilterElement(velocityFilter = velocityFilter)

        val input = KineticState(10.0, 20.0, 30.0)
        val expected = KineticState(10.0, 2.0, 30.0)

        every { velocityFilter.filter(20.0) } returns 2.0

        // Act
        val actual = filterElement.filter(input)

        // Assert
        actual shouldBe expected
        verify(exactly = 1) { velocityFilter.filter(20.0) }
    }

    @Test
    fun `uses custom acceleration filter`() {
        // Arrange
        val accelerationFilter = mockk<Filter>()
        val filterElement = FilterElement(accelerationFilter = accelerationFilter)

        val input = KineticState(10.0, 20.0, 30.0)
        val expected = KineticState(10.0, 20.0, 3.0)

        every { accelerationFilter.filter(30.0) } returns 3.0

        // Act
        val actual = filterElement.filter(input)

        // Assert
        actual shouldBe expected
        verify(exactly = 1) { accelerationFilter.filter(30.0) }
    }

    @Test
    fun `uses all three custom filters`() {
        // Arrange
        val positionFilter = mockk<Filter>()
        val velocityFilter = mockk<Filter>()
        val accelerationFilter = mockk<Filter>()
        val filterElement = FilterElement(
            positionFilter = positionFilter,
            velocityFilter = velocityFilter,
            accelerationFilter = accelerationFilter
        )

        val input = KineticState(10.0, 20.0, 30.0)
        val expected = KineticState(1.0, 2.0, 3.0)

        every { positionFilter.filter(10.0) } returns 1.0
        every { velocityFilter.filter(20.0) } returns 2.0
        every { accelerationFilter.filter(30.0) } returns 3.0

        // Act
        val actual = filterElement.filter(input)

        // Assert
        actual shouldBe expected
        verify(exactly = 1) { positionFilter.filter(10.0) }
        verify(exactly = 1) { velocityFilter.filter(20.0) }
        verify(exactly = 1) { accelerationFilter.filter(30.0) }
    }
}