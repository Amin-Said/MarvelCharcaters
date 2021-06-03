package com.amin.marvelcharcaters.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EventsResponse(
    val attributionHTML: String,
    val attributionText: String,
    val code: Int,
    val copyright: String,
    val `data`: EventsData,
    val etag: String,
    val status: String
): Parcelable

@Parcelize
data class Characters(
    val available: Int,
    val collectionURI: String,
    val items: List<Item>,
    val returned: Int
): Parcelable

//data class Comics(
//    val available: Int,
//    val collectionURI: String,
//    val items: List<ItemX>,
//    val returned: Int
//)

//data class Creators(
//    val available: Int,
//    val collectionURI: String,
//    val items: List<ItemXX>,
//    val returned: Int
//)

@Parcelize
data class EventsData(
    val count: Int,
    val limit: Int,
    val offset: Int,
    val results: List<EventsResult>,
    val total: Int
): Parcelable

//data class Item(
//    val name: String,
//    val resourceURI: String
//)

//data class ItemX(
//    val name: String,
//    val resourceURI: String
//)
//
//data class ItemXX(
//    val name: String,
//    val resourceURI: String,
//    val role: String
//)

//data class ItemXXX(
//    val name: String,
//    val resourceURI: String
//)
//
//data class ItemXXXX(
//    val name: String,
//    val resourceURI: String,
//    val type: String
//)


@Parcelize
data class Next(
    val name: String,
    val resourceURI: String
): Parcelable

@Parcelize
data class Previous(
    val name: String,
    val resourceURI: String
): Parcelable

@Parcelize
data class EventsResult(
    val characters: Characters,
    val comics: Comics,
    val creators: Creators,
    val description: String,
    val end: String,
    val id: Int,
    val modified: String,
    val next: Next,
    val previous: Previous,
    val resourceURI: String,
    val series: Series,
    val start: String,
    val stories: Stories,
    val thumbnail: Thumbnail,
    val title: String,
    val urls: List<Url>
): Parcelable

//data class Series(
//    val available: Int,
//    val collectionURI: String,
//    val items: List<ItemXXX>,
//    val returned: Int
//)
//
//data class Stories(
//    val available: Int,
//    val collectionURI: String,
//    val items: List<ItemXXXX>,
//    val returned: Int
//)