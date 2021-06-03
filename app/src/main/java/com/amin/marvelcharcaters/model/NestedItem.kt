package com.amin.marvelcharcaters.model

data class NestedItem(
    val requestedTitle: String,
    val list: List<PosterItem>
)