package com.kanthi.githubrepoexplorer.core.util

import org.junit.Assert.assertEquals
import org.junit.Test

class CountFormatterTest {

    @Test
    fun `counts under 1000 render as-is`() {
        assertEquals("0", CountFormatter.format(0))
        assertEquals("42", CountFormatter.format(42))
        assertEquals("999", CountFormatter.format(999))
    }

    @Test
    fun `counts in the thousands render with a k suffix`() {
        assertEquals("1k", CountFormatter.format(1000))
        assertEquals("12.5k", CountFormatter.format(12_500))
        assertEquals("999.9k", CountFormatter.format(999_949))
    }

    @Test
    fun `counts in the millions render with an m suffix`() {
        assertEquals("1m", CountFormatter.format(1_000_000))
        assertEquals("2.3m", CountFormatter.format(2_340_000))
    }

    @Test
    fun `trailing zero decimals are trimmed, not just the k or m ones`() {
        assertEquals("10k", CountFormatter.format(10_000))
        assertEquals("100k", CountFormatter.format(100_049))
    }

    @Test
    fun `negative counts format using their magnitude`() {
        assertEquals("-12.5k", CountFormatter.format(-12_500))
    }
}
