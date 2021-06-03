package com.amin.marvelcharcaters.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SeriesResponse(
    val attributionHTML: String,
    val attributionText: String,
    val code: Int,
    val copyright: String,
    val `data`: SeriesData,
    val etag: String,
    val status: String
): Parcelable

@Parcelize
data class SeriesData(
    val count: Int,
    val limit: Int,
    val offset: Int,
    val results: List<SeriesResult>,
    val total: Int
): Parcelable

@Parcelize
data class SeriesResult(
    val characters: Characters,
    val comics: Comics,
    val creators: SeriesCreators,
    val description: String,
    val endYear: Int,
    val events: Events,
    val id: Int,
    val modified: String,
    val next: Int,
    val previous: Int,
    val rating: String,
    val resourceURI: String,
    val startYear: Int,
    val stories: Stories,
    val thumbnail: Thumbnail,
    val title: String,
    val type: String,
    val urls: List<Url>
): Parcelable

@Parcelize
data class SeriesCreators(
    val available: Int,
    val collectionURI: String,
    val items: List<SeriesItemCreator>,
    val returned: Int
): Parcelable

@Parcelize
data class SeriesItemCreator(
    val name: String,
    val resourceURI: String,
    val role: String
): Parcelable

@Parcelize
data class OriginalIssue(
    val name: String,
    val resourceURI: String
): Parcelable