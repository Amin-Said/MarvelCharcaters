package com.amin.marvelcharcaters.model.details

data class NestedItem(
    val requestedTitle: String,
    val list: List<PosterItem>
)