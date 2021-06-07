package com.amin.marvelcharcaters

import com.amin.marvelcharcaters.model.*
import com.amin.marvelcharcaters.model.details.*
import com.amin.marvelcharcaters.model.details.Series


@Suppress("MaxLineLength")
object MockData {

    val item = Item("name", "resource", "type")
    val comics = Comics(20, "", listOf(item), 20)
    val stories = Stories(20, "", listOf(item), 20)
    val events = Events(20, "", listOf(item), 20)
    val series = com.amin.marvelcharcaters.model.Series(20, "", listOf(item), 20)
    val thumbnail = Thumbnail("url", "format")
    val url = Url("type", "url")
    val result = CharacterResult(
        comics, "details", events, 24, "", "Spider Man", "url", series, stories,
        thumbnail, listOf(url)
    )
    val data = com.amin.marvelcharcaters.model.Data(20, 20, 8, listOf(result), 100)
    val characterResponse = CharacterResponse("", "", 1, "mock", data, "tag", "status")

    const val timestamp = "11111"
    const val hash = "22222"
    const val key = "3333"
    const val query = "3-D Man"
    const val page = "0"

    const val resourceURL = "resourceURL"

    val resourceSeries = Series("name","url")


    val resourceResult = ResourceResult("name",
        Characters(1, "", listOf(Item("name", "", "")), 1),
        emptyList(), emptyList(), Creators(1,"", emptyList(),1), emptyList(), comics,"","",""
    ,1,"", events,"",20,Next("",""), Previous("",""),"", emptyList(),"",
        "",12,"",20, emptyList(),"", resourceSeries, stories, emptyList(),Thumbnail("extension","jpg"),
        "title","", emptyList(),20,"5",14, OriginalIssue("",""),"", emptyList())
    val resourceData =
        com.amin.marvelcharcaters.model.details.Data(20, 20, 8, listOf(resourceResult), 100)

    val resourceResponse = ResourceResponse("", "", 1, "mock", resourceData, "tag", "status")


}

