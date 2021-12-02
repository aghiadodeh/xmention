package com.aghiadodehgithub.mention

import android.content.Context
import android.graphics.Typeface.BOLD
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import com.aghiadodehgithub.mention.R
import java.util.*
import kotlin.collections.ArrayList


object SpanUtils {
    fun styleWord(textView: TextView, searchText: ArrayList<String>, searchWords: ArrayList<String>) {
        if (searchWords.isNotEmpty()) {
            val newText: Spannable = setSpanHighlight(textView.context, getArrayAsString(searchWords), searchText)
            textView.setText(newText, TextView.BufferType.SPANNABLE)
        } else {
            textView.text = getArrayAsString(searchWords)
        }
    }

    private fun setSpanHighlight(context: Context, text: String, @NonNull searchWord: ArrayList<String>): Spannable {
        val color = ContextCompat.getColor(context, R.color.purple_500)
        val newText: Spannable = SpannableString(text)
        if (searchWord.size != 0) {
            for (word in searchWord) {
                if (text.lowerCase().contains(word.lowerCase())) {
                    val beginIndex = text.lowerCase().indexOf(word.lowerCase())
                    val endIndex = beginIndex + word.trim().length
                    newText.setSpan(ForegroundColorSpan(color), beginIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    newText.setSpan(StyleSpan(BOLD), beginIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
        }
        return newText
    }

    private fun getArrayAsString(array: ArrayList<String>): String {
        var text = ""
        for (x in 0 until array.size) text = if (x != array.size - 1) "${array[x]}\n" else array[x]
        return text
    }

    fun toLowerCase(string: String) = string.lowercase(Locale.getDefault())

    private fun String.lowerCase() : String = this.lowercase(Locale.getDefault()).trim()

    private fun String.lowerTrim() : String = this.lowercase(Locale.getDefault()).replace(" ", "")
}