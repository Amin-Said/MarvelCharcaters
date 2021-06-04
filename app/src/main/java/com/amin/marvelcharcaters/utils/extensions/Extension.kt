package com.amin.marvelcharcaters.utils.extensions

import android.content.Context
import android.content.res.AssetManager
import android.widget.Toast
import com.amin.marvelcharcaters.model.*

fun AssetManager.readFile(fileName: String) = open(fileName)
    .bufferedReader()
    .use { it.readText() }

// the next methods to read images as http because https not loading // now this fixed in manifest using android:usesCleartextTraffic="true"
fun CharacterResult.getImage(path:String,extension: String):String{
     return "https"+path.substring(4)+"."+extension
}

fun Context.toast(message: CharSequence) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun Context.getString(id: Int) =
    this.getString(id)

fun Context.toastFromResource(resourceID:Int) =
    Toast.makeText(this, this.getString(resourceID), Toast.LENGTH_SHORT).show()


