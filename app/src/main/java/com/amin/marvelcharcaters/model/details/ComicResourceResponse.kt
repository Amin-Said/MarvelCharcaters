package com.amin.marvelcharcaters.model.details

import com.amin.marvelcharcaters.model.*

data class ComicResourceResponse(
    val attributionHTML: String,
    val attributionText: String,
    val code: Int,
    val copyright: String,
    val `data`: Data,
    val etag: String,
    val status: String
)

data class Characters(
    val available: Int,
    val collectionURI: String,
    val items: List<Item>,
    val returned: Int
)

data class Creators(
    val available: Int,
    val collectionURI: String,
    val items: List<Item>,
    val returned: Int
)

data class Data(
    val count: Int,
    val limit: Int,
    val offset: Int,
    val results: List<ResourceResult>,
    val total: Int
)

data class Date(
    val date: String,
    val type: String
)

data class Image(
    val extension: String,
    val path: String
)

data class Price(
    val price: Double,
    val type: String
)

data class ResourceResult(
    val name: String?,
    val characters: Characters?,
    val collectedIssues: List<Any>?,
    val collections: List<Any>?,
    val creators: Creators?,
    val dates: List<Date>?,
    val comics: Comics?,
    val end: String?,
    val description: String?,
    val diamondCode: String?,
    val digitalId: Int?,
    val ean: String?,
    val events: Events?,
    val format: String?,
    val id: Int?,
    val next: Next?,
    val previous: Previous?,
    val start: String?,
    val images: List<Image>?,
    val isbn: String?,
    val issn: String?,
    val issueNumber: Int?,
    val modified: String?,
    val pageCount: Int?,
    val prices: List<Price>?,
    val resourceURI: String?,
    val series: Series?,
    val stories: Stories?,
    val textObjects: List<TextObject>?,
    val thumbnail: Thumbnail?,
    val title: String?,
    val upc: String?,
    val urls: List<Url>?,
    val endYear: Int?,
    val rating: String?,
    val startYear: Int?,
    val originalIssue: OriginalIssue?,
    val variantDescription: String?,
    val variants: List<Variant>?
)
data class Series(
    val name: String,
    val resourceURI: String
)

data class TextObject(
    val language: String,
    val text: String,
    val type: String
)

data class Variant(
    val name: String,
    val resourceURI: String
)

data class Next(
    val name: String,
    val resourceURI: String
)

data class Previous(
    val name: String,
    val resourceURI: String
)

data class OriginalIssue(
    val name: String,
    val resourceURI: String
)