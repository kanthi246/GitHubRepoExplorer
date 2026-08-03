package com.kanthi.githubrepoexplorer.core.util

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

    private fun trimTrailingZero(value: Double): String {
        val rounded = "%.1f".format(value)
        return if (rounded.endsWith(".0")) rounded.dropLast(2) else rounded
    }
}
