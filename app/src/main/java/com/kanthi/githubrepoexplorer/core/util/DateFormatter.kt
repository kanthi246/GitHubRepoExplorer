package com.kanthi.githubrepoexplorer.core.util

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

/**
 * GitHub's API returns ISO-8601 UTC timestamps ("2024-05-12T10:15:23Z"); this renders them for
 * display in the device's local time zone and locale (e.g. "May 12, 2024").
 *
 * Benefit: the network/domain layers keep passing around the raw, unambiguous ISO string, and
 * only the UI layer — which is the only place that cares about human presentation — converts it.
 */
object DateFormatter {

    private val displayFormat = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault())

    fun format(isoTimestamp: String): String = try {
        Instant.parse(isoTimestamp).atZone(ZoneId.systemDefault()).format(displayFormat)
    } catch (e: DateTimeParseException) {
        isoTimestamp
    }
}
