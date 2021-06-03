package com.amin.marvelcharcaters.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StoriesResponse(
    val attributionHTML: String,
    val attributionText: String,
    val code: Int,
    val copyright: String,
    val `data`: DetailsData,
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
    val thumbnail: Thumbnail,
    val title: String,
    val type: String
): Parcelable

@Parcelize
data class StoriesData(
    val count: Int,
    val limit: Int,
    val offset: Int,
    val results: List<BaseResult>,
    val total: Int
): Parcelable



