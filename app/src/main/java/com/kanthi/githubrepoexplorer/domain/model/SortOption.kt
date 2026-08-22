package com.kanthi.githubrepoexplorer.domain.model

/** The ways search results can be sorted. `apiValue` is what GitHub's API expects in the URL; `label` is what the UI displays — keeping both on one enum avoids a separate mapping table elsewhere. */
enum class SortOption(val apiValue: String?, val label: String) {
    BEST_MATCH(null, "Best match"),
    STARS("stars", "Stars"),
    FORKS("forks", "Forks"),
    UPDATED("updated", "Recently updated"),
}
