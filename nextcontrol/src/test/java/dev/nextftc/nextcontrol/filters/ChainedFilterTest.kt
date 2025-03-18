package dev.nextftc.nextcontrol.filters

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.Test

class ChainedFilterTest {

    @Test
    fun `filters through filters in the order they were passed in the constructor`() {
        // Arrange
        val filter1 = mockk<Filter>()
        every { filter1.filter(0.0) } returns 1.0

        val filter2 = mockk<Filter>()
        every { filter2.filter(1.0) } returns 2.0

        val expected = 2.0

        // Act
        val chainedFilter = ChainedFilter(filter1, filter2)
        val actual = chainedFilter.filter(0.0)

        // Assert
        assertEquals(expected, actual, 0.0)
        verify(exactly = 1) { filter1.filter(0.0) }
        verify(exactly = 1) { filter2.filter(1.0) }
    }

    @Test
    fun `throws exception when no filters are given`() {
        // Assert
        assertFailsWith<IllegalArgumentException> {
            ChainedFilter()
        }
    }

    @Test
    fun `works with more than two filters`() {
        // Arrange
        val filter1 = mockk<Filter>()
        every { filter1.filter(0.0) } returns 1.0

        val filter2 = mockk<Filter>()
        every { filter2.filter(1.0) } returns 2.0

        val filter3 = mockk<Filter>()
        every { filter3.filter(2.0) } returns 3.0

        val filter4 = mockk<Filter>()
        every { filter4.filter(3.0) } returns 4.0

        val filter5 = mockk<Filter>()
        every { filter5.filter(4.0) } returns 5.0

        val expected = 5.0

        // Act
        val chainedFilter = ChainedFilter(filter1, filter2, filter3, filter4, filter5)
        val actual = chainedFilter.filter(0.0)

        // Assert
        assertEquals(expected, actual, 0.0)
        verify(exactly = 1) { filter1.filter(0.0) }
        verify(exactly = 1) { filter2.filter(1.0) }
        verify(exactly = 1) { filter3.filter(2.0) }
        verify(exactly = 1) { filter4.filter(3.0) }
        verify(exactly = 1) { filter5.filter(4.0) }
    }
}