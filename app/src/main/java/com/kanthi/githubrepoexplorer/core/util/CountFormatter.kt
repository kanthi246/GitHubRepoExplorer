package com.kanthi.githubrepoexplorer.core.util

import java.util.Locale
import kotlin.math.abs

/** Renders large counts compactly, GitHub-style: 12,500 -> "12.5k". */
object CountFormatter {

    fun format(count: Int): String {
        val absCount = abs(count)
        return when {
            absCount < 1000 -> count.toString()
            absCount < 1_000_000 -> "${trimTrailingZero(count / 1000.0)}k"
            else -> "${trimTrailingZero(count / 1_000_000.0)}m"
        }
    }

    // Always renders with a "." separator (Locale.US), regardless of device locale — this is a
    // compact numeric abbreviation like GitHub's own UI, not a localized number.
    private fun trimTrailingZero(value: Double): String {
        val rounded = String.format(Locale.US, "%.1f", value)
        return if (rounded.endsWith(".0")) rounded.dropLast(2) else rounded
    }
}
