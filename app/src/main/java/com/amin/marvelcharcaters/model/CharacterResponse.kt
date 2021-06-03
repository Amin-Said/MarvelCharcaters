package com.amin.marvelcharcaters.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CharacterResponse(
    val attributionHTML: String,
    val attributionText: String,
    val code: Int,
    val copyright: String,
    val `data`: Data,
    val etag: String,
    val status: String
): Parcelable

@Parcelize
data class Comics(
    val available: Int,
    val collectionURI: String,
    val items: List<DefaultItem>,
    val returned: Int
): Parcelable
@Parcelize
data class Data(
    val count: Int,
    val limit: Int,
    val offset: Int,
    val results: List<CharacterResult>,
    val total: Int
): Parcelable

@Parcelize
data class Events(
    val available: Int,
    val collectionURI: String,
    val items: List<DefaultItem>,
    val returned: Int
): Parcelable

@Parcelize
data class DefaultItem(
    val name: String,
    val resourceURI: String
): Parcelable

@Parcelize
data class StoriesItem(
    val name: String,
    val resourceURI: String,
    val type: String
): Parcelable
@Parcelize
data class CharacterResult(
    val comics: Comics,
    val description: String,
    val events: Events,
    val id: Int,
    val modified: String,
    val name: String,
    val resourceURI: String,
    val series: Series,
    val stories: Stories,
    val thumbnail: Thumbnail,
    val urls: List<Url>
): Parcelable
@Parcelize
data class Series(
    val available: Int,
    val collectionURI: String,
    val items: List<DefaultItem>,
    val returned: Int
): Parcelable
@Parcelize
data class Stories(
    val available: Int,
    val collectionURI: String,
    val items: List<StoriesItem>,
    val returned: Int
): Parcelable

@Parcelize
data class Thumbnail(
    val extension: String,
    val path: String
): Parcelable

@Parcelize
data class Url(
    val type: String,
    val url: String
): Parcelable