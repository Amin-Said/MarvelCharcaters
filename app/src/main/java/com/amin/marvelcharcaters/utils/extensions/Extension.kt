package com.amin.marvelcharcaters.utils.extensions

import android.content.res.AssetManager
import androidx.appcompat.widget.SearchView
import com.amin.marvelcharcaters.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

fun AssetManager.readFile(fileName: String) = open(fileName)
    .bufferedReader()
    .use { it.readText() }

// the next methods to read images as http because https not loading // now this fixed in manifest using android:usesCleartextTraffic="true"
fun CharacterResult.getImage(path:String,extension: String):String{
     return "https"+path.substring(4)+"."+extension
}

fun BaseResult.getImage(path:String,extension: String):String{
    return "https"+path.substring(4)+"."+extension
}

fun StoriesResult.getImage(path:String,extension: String):String{
    return "https"+path.substring(4)+"."+extension
}

fun SeriesResult.getImage(path:String,extension: String):String{
    return "https"+path.substring(4)+"."+extension
}

fun EventsResult.getImage(path:String,extension: String):String{
    return "https"+path.substring(4)+"."+extension
}

fun SearchView.getQueryTextChangeStateFlow(): StateFlow<String> {

    val query = MutableStateFlow("")

    setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            return true
        }

        override fun onQueryTextChange(newText: String): Boolean {
            query.value = newText
            return true
        }
    })

    return query

}