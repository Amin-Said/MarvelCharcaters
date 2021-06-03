package com.amin.marvelcharcaters.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StoriesResponse(
    val attributionHTML: String,
    val attributionText: String,
    val code: Int,
    val copyright: String,
    val `data`: StoriesData,
    val etag: String,
    val status: String
): Parcelable

@Parcelize
data class StoriesResult(
    val characters: Characters,
    val comics: Comics,
    val creators: Creators,
    val description: String,
    val events: Events,
    val id: Int,
    val modified: String,
    val originalIssue: OriginalIssue,
    val resourceURI: String,
    val series: Series,
    val thumbnail: String,
    val title: String,
    val type: String
): Parcelable

@Parcelize
data class StoriesData(
    val count: Int,
    val limit: Int,
    val offset: Int,
    val results: List<StoriesResult>,
    val total: Int
): Parcelable



