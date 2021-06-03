package com.amin.marvelcharcaters.utils

import android.content.res.AssetManager
import com.amin.marvelcharcaters.model.CharacterResult

fun AssetManager.readFile(fileName: String) = open(fileName)
    .bufferedReader()
    .use { it.readText() }

fun CharacterResult.getImage(path:String,extension: String):String{
     return "https"+path.substring(4)+"."+extension
}