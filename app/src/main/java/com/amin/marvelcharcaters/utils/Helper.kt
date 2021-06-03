package com.amin.marvelcharcaters.utils

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.BackgroundColorSpan
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.core.graphics.ColorUtils
object Helper{

public fun buildHighlightString(originalText: String, textToHighlight: String): SpannableString? {
    return buildHighlightString(originalText, textToHighlight, true, Color.RED, 0.2f)
}

/**
 * Build a spannable String for use in highlighting text colors
 *
 * @param originalText The original text that is being highlighted
 * @param textToHighlight The text / query that determines what to highlight
 * @param ignoreCase Whether or not to ignore case. If true, will ignore and "test" will have
 * the same return as "TEST". If false, will return an item as highlighted
 * only if it matches it case specficic.
 * @param highlightColor The highlight color to use. IE [Color.YELLOW] || [Color.BLUE]
 * @param colorAlpha Alpha to adjust how transparent the color is. 1.0 means it looks exactly
 * as it should normally where as 0.0 means it is completely transparent and
 * see-through. 0.5 means it is 50% transparent. Useful for darker colors
 */
fun buildHighlightString(
    originalText: String, textToHighlight: String,
    ignoreCase: Boolean, @ColorInt highlightColor: Int,
    @FloatRange(from = 0.0, to = 1.0) colorAlpha: Float
): SpannableString? {
    var highlightColor = highlightColor
    val spannableString = SpannableString(originalText)
    if (TextUtils.isEmpty(originalText) || TextUtils.isEmpty(textToHighlight)) {
        return spannableString
    }
    val lowercaseOriginalString = originalText.toLowerCase()
    val lowercaseTextToHighlight = textToHighlight.toLowerCase()
    if (colorAlpha < 1) {
        highlightColor = ColorUtils.setAlphaComponent(highlightColor, (255 * colorAlpha).toInt())
    }
    //Get the previous spans and remove them
    val backgroundSpans = spannableString.getSpans(
        0, spannableString.length,
        BackgroundColorSpan::class.java
    )
    for (span in backgroundSpans) {
        spannableString.removeSpan(span)
    }
    //Search for all occurrences of the keyword in the string
    var indexOfKeyword =
        if (ignoreCase) lowercaseOriginalString.indexOf(lowercaseTextToHighlight) else originalText.indexOf(
            textToHighlight
        )
    while (indexOfKeyword != -1) {
        //Create a background color span on the keyword
        spannableString.setSpan(
            BackgroundColorSpan(highlightColor), indexOfKeyword,
            indexOfKeyword + textToHighlight.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        //Get the next index of the keyword
        indexOfKeyword = if (ignoreCase) lowercaseOriginalString.indexOf(
            lowercaseTextToHighlight,
            indexOfKeyword + textToHighlight.length
        ) else originalText.indexOf(textToHighlight, indexOfKeyword + textToHighlight.length)
    }
    return spannableString
}
}
