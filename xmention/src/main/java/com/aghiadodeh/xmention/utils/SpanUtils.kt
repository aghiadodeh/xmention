package com.aghiadodeh.xmention.utils

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.NonNull
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.ArrayList

object SpanUtils {
    fun styleWord(textView: TextView, searchText: List<String>, searchWords: ArrayList<String>, @ColorInt color: Int) {
        /*val searchSpan: ArrayList<String> = ArrayList(listOf(searchText))*/
        if (searchWords.isNotEmpty()) {
            val newText: Spannable = setSpanHighlight(getArrayAsString(searchWords), searchText, color)
            textView.setText(newText, TextView.BufferType.SPANNABLE)
        } else {
            textView.text = getArrayAsString(searchWords)
        }
    }

    private fun setSpanHighlight(originalText: String, @NonNull searchWord: List<String>, @ColorInt color: Int): Spannable {
        var text = "$originalText "
        val newText: Spannable = SpannableString(originalText)
        if (searchWord.isNotEmpty()) {
            for (word in searchWord) {
                if (toLowerCase(text).contains(toLowerCase(word))) {
                    val beginIndex = toLowerCase(text).indexOf(toLowerCase(word))
                    val endIndex = beginIndex + word.trim().length
                    val stringBuilder = StringBuilder()
                    word.map { stringBuilder.append('@') }
                    text = text.replaceFirst(word, stringBuilder.toString()) // avoid repeated words, so replace colored word
                    newText.setSpan(ForegroundColorSpan(color), beginIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    newText.setSpan(StyleSpan(Typeface.BOLD), beginIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
        }
        return newText
    }

    fun highlightText(textView: TextView, text: String, begin: Int, end: Int, @ColorInt color: Int) {
        val newText: Spannable = SpannableString(text)
        try {
            newText.setSpan(ForegroundColorSpan(color), begin, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            newText.setSpan(StyleSpan(Typeface.BOLD), begin, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            textView.setText(newText, TextView.BufferType.SPANNABLE)
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
            Log.e("highlightText", e.toString())
        }
    }

    private fun getArrayAsString(array: ArrayList<String>): String {
        var text = ""
        for (x in 0 until array.size) text = if (x != array.size - 1) "${array[x]}\n" else array[x]
        return text
    }

    fun toLowerCase(string: String) = string.lowercase(Locale.getDefault()).trim()
}