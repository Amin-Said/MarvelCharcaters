package com.amin.marvelcharcaters.model.comicresource

data class Data(
    val count: Int,
    val limit: Int,
    val offset: Int,
    val results: List<ResourceResult>,
    val total: Int
)