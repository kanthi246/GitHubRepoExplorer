package com.kanthi.githubrepoexplorer.core.util

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.Locale
import java.util.TimeZone

class DateFormatterTest {

    private lateinit var originalLocale: Locale
    private lateinit var originalTimeZone: TimeZone

    @Before
    fun fixLocaleAndTimeZone() {
        // DateFormatter deliberately renders in the device's locale/zone, so pin both here to
        // make the test deterministic regardless of the machine running it.
        originalLocale = Locale.getDefault()
        originalTimeZone = TimeZone.getDefault()
        Locale.setDefault(Locale.US)
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    @After
    fun restoreLocaleAndTimeZone() {
        Locale.setDefault(originalLocale)
        TimeZone.setDefault(originalTimeZone)
    }

    @Test
    fun `formats an ISO-8601 timestamp as a readable date`() {
        assertEquals("May 12, 2024", DateFormatter.format("2024-05-12T10:15:23Z"))
    }

    @Test
    fun `formats single-digit days without zero padding`() {
        assertEquals("Jan 3, 2026", DateFormatter.format("2026-01-03T00:00:00Z"))
    }

    @Test
    fun `falls back to the raw string when it cannot be parsed`() {
        assertEquals("not-a-date", DateFormatter.format("not-a-date"))
    }

    @Test
    fun `falls back to the raw string for a blank input`() {
        assertEquals("", DateFormatter.format(""))
    }
}
