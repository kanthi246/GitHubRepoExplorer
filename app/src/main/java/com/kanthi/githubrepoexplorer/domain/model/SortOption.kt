package com.kanthi.githubrepoexplorer.domain.model

enum class SortOption(val apiValue: String?, val label: String) {
    BEST_MATCH(null, "Best match"),
    STARS("stars", "Stars"),
    FORKS("forks", "Forks"),
    UPDATED("updated", "Recently updated"),
}
