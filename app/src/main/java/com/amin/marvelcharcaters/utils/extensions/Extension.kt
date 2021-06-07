package com.amin.marvelcharcaters.utils.extensions

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.AssetManager
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.BaseInterpolator
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.amin.marvelcharcaters.model.*

// to read from assets json files
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

fun RecyclerView.ViewHolder.addAnimation(interpolator: BaseInterpolator, lastPosition: Int, duration:Long){
    var lastPosition = -1
    val delayTime: Long = 200
    this.itemView.visibility = View.INVISIBLE
    if (this.layoutPosition > lastPosition) {
        this.itemView.handler.postDelayed(Runnable {
            this.itemView.visibility = View.VISIBLE
            val alpha = ObjectAnimator.ofFloat(this.itemView, "alpha", 0f, 1f)
            val scaleY =
                ObjectAnimator.ofFloat(this.itemView, "scaleY", 0f, 1f)
            val scaleX =
                ObjectAnimator.ofFloat(this.itemView, "scaleX", 0f, 1f)
            val animSet = AnimatorSet()
            animSet.play(alpha).with(scaleY).with(scaleX)
            animSet.interpolator = interpolator
            animSet.duration = duration
            animSet.start()
        }, delayTime)
        lastPosition = this.layoutPosition
    } else {
        this.itemView.visibility = View.VISIBLE
    }

}


