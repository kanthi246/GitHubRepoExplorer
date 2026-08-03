package com.kanthi.githubrepoexplorer.core.util

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

/** GitHub's API returns ISO-8601 UTC timestamps ("2024-05-12T10:15:23Z"); this renders them for display. */
object DateFormatter {

    private val displayFormat = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault())

    fun format(isoTimestamp: String): String = try {
        Instant.parse(isoTimestamp).atZone(ZoneId.systemDefault()).format(displayFormat)
    } catch (e: DateTimeParseException) {
        isoTimestamp
    }
}
