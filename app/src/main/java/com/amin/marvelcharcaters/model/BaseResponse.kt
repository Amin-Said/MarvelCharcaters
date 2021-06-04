package com.amin.marvelcharcaters.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

// shared data between home and details models
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

@Parcelize
data class Item(
    val name: String,
    val resourceURI: String,
    val type: String
): Parcelable

@Parcelize
data class Series(
    val available: Int,
    val collectionURI: String,
    val items: List<Item>,
    val returned: Int
): Parcelable
@Parcelize
data class Stories(
    val available: Int,
    val collectionURI: String,
    val items: List<Item>,
    val returned: Int
): Parcelable

@Parcelize
data class Events(
    val available: Int,
    val collectionURI: String,
    val items: List<Item>,
    val returned: Int
): Parcelable

@Parcelize
data class Comics(
    val available: Int,
    val collectionURI: String,
    val items: List<Item>,
    val returned: Int
): Parcelable