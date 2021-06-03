package com.amin.marvelcharcaters.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class BaseResponse(
    val attributionHTML: String,
    val attributionText: String,
    val code: Int,
    val copyright: String,
    val `data`: DetailsData,
    val etag: String,
    val status: String
): Parcelable

@Parcelize
data class ComicResponse(
    val attributionHTML: String,
    val attributionText: String,
    val code: Int,
    val copyright: String,
    val `data`: ComicData,
    val etag: String,
    val status: String
): Parcelable
//data class ComicCharacters(
//    val available: Int,
//    val collectionURI: String,
//    val items: List<Item>,
//    val returned: Int
//)

@Parcelize
data class Creators(
    val available: Int,
    val collectionURI: String,
    val items: List<CreatorsItem>,
    val returned: Int
): Parcelable

@Parcelize
data class ComicData(
    val count: Int,
    val limit: Int,
    val offset: Int,
    val results: List<ComicResult>,
    val total: Int
): Parcelable

@Parcelize
data class DetailsData(
    val count: Int,
    val limit: Int,
    val offset: Int,
    val results: List<BaseResult>,
    val total: Int
): Parcelable


@Parcelize
data class Date(
    val date: String,
    val type: String
): Parcelable

//data class Events(
//    val available: Int,
//    val collectionURI: String,
//    val items: List<Any>,
//    val returned: Int
//)

@Parcelize
data class Image(
    val extension: String,
    val path: String
): Parcelable

@Parcelize
data class Item(
    val name: String,
    val resourceURI: String
): Parcelable

@Parcelize
data class CreatorsItem(
    val name: String,
    val resourceURI: String,
    val role: String
): Parcelable

//data class ItemXX(
//    val name: String,
//    val resourceURI: String,
//    val type: String
//)

@Parcelize
data class Price(
    val price: Double,
    val type: String
): Parcelable

@Parcelize
data class ComicResult(
    val characters: Comics,
    val collectedIssues: List<String>,
    val collections: List<String>,
    val creators: Creators,
    val dates: List<Date>,
    val description: String,
    val diamondCode: String,
    val digitalId: Int,
    val ean: String,
    val events: Events,
    val format: String,
    val id: Int,
    val images: List<Image>,
    val isbn: String,
    val issn: String,
    val issueNumber: Int,
    val modified: String,
    val pageCount: Int,
    val prices: List<Price>,
    val resourceURI: String,
    val series: Series,
    val stories: Stories,
    val textObjects: List<TextObject>,
    val thumbnail: Thumbnail,
    val title: String,
    val upc: String,
    val urls: List<Url>,
    val variantDescription: String,
    val variants: List<String>
): Parcelable

@Parcelize
data class BaseResult(
    val name: String?,
    val characters: Comics?,
    val collectedIssues: List<String>?,
    val collections: List<String>?,
    val comics: Comics?,
    val end: String?,
    val creators: Creators?,
    val dates: List<Date>?,
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
    val stories: Stories?,
    val textObjects: List<TextObject>?,
    val thumbnail: Thumbnail?,
    val title: String?,
    val upc: String?,
    val urls: List<Url>?,
    val variantDescription: String?,
    val variants: List<String>?,
    val endYear: Int?,
    val rating: String?,
    val startYear: Int?,
    val originalIssue: OriginalIssue?
): Parcelable

//data class ComicSeries(
//    val name: String,
//    val resourceURI: String
//)

//data class Stories(
//    val available: Int,
//    val collectionURI: String,
//    val items: List<ItemXX>,
//    val returned: Int
//)

@Parcelize
data class TextObject(
    val language: String,
    val text: String,
    val type: String
): Parcelable

//data class Thumbnail(
//    val extension: String,
//    val path: String
//)

//data class Url(
//    val type: String,
//    val url: String
//)