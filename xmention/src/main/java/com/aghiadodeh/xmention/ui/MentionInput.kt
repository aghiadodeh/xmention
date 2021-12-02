package com.aghiadodeh.xmention.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.Html
import android.util.AttributeSet
import android.util.Log
import android.widget.EditText
import android.widget.RelativeLayout
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.aghiadodeh.xmention.R
import com.aghiadodeh.xmention.data.MentionConfig
import com.aghiadodeh.xmention.interfaces.IMention
import com.aghiadodeh.xmention.utils.MentionUtils
import com.aghiadodeh.xmention.utils.MentionUtils.readInstanceProperty
import com.aghiadodeh.xmention.utils.SpanUtils
import com.google.android.material.textfield.TextInputLayout
import com.jakewharton.rxbinding2.widget.textChanges
import com.vanniktech.emoji.EmojiEditText
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.EmojiPopup
import com.vanniktech.emoji.ios.IosEmojiProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import java.util.regex.Matcher
import java.util.regex.Pattern

class MentionInput<T>(context: Context, attrs: AttributeSet?) : RelativeLayout(context, attrs) {
    private val tag = "MentionInput"
    private var textInputLayout: TextInputLayout
    private var editText: EmojiEditText
    private var trigger = '@'
    private lateinit var mentionList: MentionList
    private var popupBuilder: EmojiPopup.Builder
    private var emojiPopup: EmojiPopup
    private var mentionInputHelper: MentionInputHelper<T>
    private var editTextIcon: Boolean = true
    fun textInputLayout(): TextInputLayout = textInputLayout
    fun editText(): EditText = editText
    fun originalText(): String = editText.text.toString()
    fun parsedString(): String = mentionInputHelper.parseString()
    fun mentionedList(): List<T> = mentionInputHelper.list

    @ColorInt
    private var color: Int = Color.BLUE

    @SuppressLint("UseCompatLoadingForDrawables")
    private var emojiDrawable: Drawable? = ContextCompat.getDrawable(context, R.drawable.ic_emoji)
    private var keyboardDrawable: Drawable? = ContextCompat.getDrawable(context, R.drawable.ic_keyboard)

    init {
        EmojiManager.install(IosEmojiProvider())
        inflate(context, R.layout.mention_input, this)
        textInputLayout = findViewById(R.id.mention_inputLayout)
        editText = findViewById(R.id.mention_editText)
        popupBuilder = EmojiPopup.Builder.fromRootView(rootView).apply { emojiPopup = build(editText) }
        initAttributes(attrs)
        mentionInputHelper = MentionInputHelper(trigger = trigger)
        initEvents()
    }

    private fun initAttributes(attrs: AttributeSet?) {
        val resources = context.resources
        val array = context.obtainStyledAttributes(attrs, R.styleable.MentionInput, 0, 0)
        array.getString(R.styleable.MentionInput_input_trigger)?.let { trigger = it.toCharArray().first() }
        array.getString(R.styleable.MentionInput_input_hint_text)?.let { editText.hint = it }
        array.getColor(R.styleable.MentionInput_input_highlight_color, ContextCompat.getColor(context, android.R.color.holo_blue_dark)).let { color = it }
        array.getDimension(R.styleable.MentionInput_input_text_size, resources.getDimension(R.dimen.medium_text)).let {
            editText.textSize = MentionUtils.pixelsToSp(context, it)
        }
        array.getColor(R.styleable.MentionInput_input_tint_color, ContextCompat.getColor(context, android.R.color.transparent)).let {
            val colorStateList = ColorStateList.valueOf(it)
            ViewCompat.setBackgroundTintList(editText, colorStateList)
        }
        array.getDrawable(R.styleable.MentionInput_input_keyboard_icon)?.let { keyboardDrawable = it }
        array.getDrawable(R.styleable.MentionInput_input_emoji_icon)?.let {
            emojiDrawable = it
            textInputLayout.endIconDrawable = it
        }
        array.getBoolean(R.styleable.MentionInput_input_show_emoji_icon, true).let { if (!it) textInputLayout.endIconDrawable = null }
        array.recycle()
    }

    @SuppressLint("CheckResult")
    private fun initEvents() {
        if (editTextIcon) {
            textInputLayout.setEndIconOnClickListener {
                if (textInputLayout.endIconDrawable == emojiDrawable) {  // show emojis
                    textInputLayout.endIconDrawable = keyboardDrawable
                    emojiPopup.show()
                } else { // show keyboard
                    textInputLayout.endIconDrawable = emojiDrawable
                    emojiPopup.dismiss()
                }
            }

            popupBuilder.setOnSoftKeyboardOpenListener { if (!emojiPopup.isShowing) textInputLayout.endIconDrawable = emojiDrawable }

            editText.setOnClickListener {// show keyboard
                textInputLayout.endIconDrawable = emojiDrawable
                emojiPopup.dismiss()
            }
        }
    }

    @SuppressLint("CheckResult")
    fun attachToMentionList(mentionList: MentionList) {
        mentionList.let { this.mentionList = it }
        val mentionData = mentionList.getMentionData()
        val recyclerView = mentionList.getRecyclerView()
        mentionList.getAdapter().setListener(iMention = iMention)
        mentionInputHelper.apply {
            this.mentionConfig = mentionData
            this.iMentionItems = this@MentionInput.iMentionHelper
            init()
        }
        recyclerView.visibility = GONE
        editText.textChanges().debounce(200, TimeUnit.MILLISECONDS).subscribe {
            MainScope().launch {
                withContext(Dispatchers.Default) {
                    //("Background processing...")
                    if (it.isNotEmpty()) {
                        mentionInputHelper.originalString = it.toString()
                        val query = getSpilt(it.toString())
                        mentionData.search(query, mentionInputHelper.list, editText.selectionEnd).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe {
                            mentionData.notifyDataSetChanged(recyclerView, getSpilt(it.toString()))
                        }
                    }
                }
                //("Update UI here!")
                if (it.isNotEmpty()) mentionInputHelper.refreshItems(it.toString())
                else recyclerView.visibility = GONE
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun appendTrigger() {
        val text = editText.text.toString()
        editText.setText("$text $trigger")
        editText.setSelection("$text $trigger".length)
        styleWords(updateCursor = false)
        editText.requestFocus()
    }

    private fun getSpilt(string: String): String {
        var query: String = ""
        val selection = editText.selectionEnd
        val text = editText.text.toString()
        val split = string.split(trigger)
        if (split.isNotEmpty()) {
            if (text.length == selection) { // editText cursor at last
                query = split.last()
            } else if (text.length > selection) { // editText cursor may in start or middle
                val beforeCursor = split.filter { getStartAndEndOfSubstring(string, it).first < selection }
                if (beforeCursor.isNotEmpty()) query = beforeCursor.last()
                // Log.e(tag, "${text.lastIndexOf(trigger)}")
                try {
                    // get only string before cursor
                    query = query.substring(0, selection)
                } catch (e: StringIndexOutOfBoundsException) {
                    e.printStackTrace()
                    // Log.e(TAG, e.toString())
                }
            }
        }
        // Log.e(tag, query)
        return query.filter { c -> c != trigger }
    }

    private fun getStartAndEndOfSubstring(str: String, sub: String): Pair<Int, Int> {
        val start = str.indexOf(sub)
        return when (start != -1) {
            true -> Pair(start, start + sub.length - 1)
            false -> Pair(-1, -1)
        }
    }

    private fun styleWords(updateCursor: Boolean) {
        val text = editText.text.toString()
        val selection = editText.selectionEnd
        SpanUtils.styleWord(textView = editText, searchText = mentionInputHelper.getTagsLabels(), searchWords = arrayListOf(editText.text.toString()), color = color)

        if (updateCursor) editText.setSelection(text.length)
        else editText.setSelection(selection)

        /*val begin = text.lastIndexOf(trigger)
        Log.e(TAG, "begin: $begin, end: ${editText.selectionEnd}")
        SpanUtils.highlightText(textView = editText, text = text, begin = begin, end = editText.selectionEnd, color = color)*/
    }

    private val iMention = object : IMention {
        @Suppress("UNCHECKED_CAST")
        override fun onSelect(item: Any) {
            if (!::mentionList.isInitialized) return
            val value = readInstanceProperty<String>(item, mentionList.getMentionData().displayField)
            val text = editText.text.toString()
            val selection = editText.selectionEnd
            val split = text.split(trigger)
            val query = getSpilt(text).split(" ").first()
            if (split.isNotEmpty()) {
                val replace = if (query.isNotEmpty()) {
                    // Log.e(tag, "query $query")
                    text.replace("$trigger${query}", "$trigger$value ")
                } else {
                    val index = text.substring(0, selection).lastIndexOf(trigger)
                    // Log.e(tag, "index $index, text $text value $value")
                    text.replaceRange(index, selection, "$trigger$value ")
                }

                editText.setText(Html.fromHtml(replace))
                // Log.e(TAG, "value: $value, text: $text, query: $query")
                mentionInputHelper.originalString = replace
                mentionInputHelper.addItem(item as T)
                styleWords(updateCursor = true)
            }
        }
    }

    private val iMentionHelper = object : MentionInputHelper.IMentionHelper {
        override fun onItemsCountChanged() {
            styleWords(updateCursor = false)
        }
    }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    private data class MentionInputHelper<T>(
        val list: MutableList<T> = mutableListOf(),
        var originalString: String = "",
        private var parsedString: String = "",
        val trigger: Char,
        var mentionConfig: MentionConfig<*>? = null,
        var iMentionItems: IMentionHelper? = null,
    ) {
        private val tag = "MentionInputHelper"
        val keys = mutableListOf<String>()

        fun init() {
            val pattern: Pattern = Pattern.compile("#(.*?);", Pattern.DOTALL)
            val matcher: Matcher = pattern.matcher("${mentionConfig?.mentionFormat}")
            while (matcher.find()) keys.add(matcher.group(1))
        }

        fun refreshItems(string: String) {
            val newLabels = getTagsLabels().filter { string.contains(it) }
            if (list.size != newLabels.size) {
                val newList = mutableListOf<T>().apply { addAll(list.filter { newLabels.contains(trigger + readProperty(it)) }) }
                list.clear()
                list.addAll(newList)
                iMentionItems?.onItemsCountChanged()
                parseString()
            }
        }

        fun addItem(item: T) {
            if (!list.contains(item)) { // avoid duplicate items
                list.add(item)
                parseString()
            }
        }

        fun getTagsLabels(): List<String> = list.map { trigger + readProperty(it) }

        private fun readProperty(item: T, field: String = mentionConfig!!.displayField): String {
            return readInstanceProperty(item as Any, field)
        }

        fun parseString(): String {
            parsedString = originalString
            val selected = mutableListOf<T>()
            list.filter { !selected.contains(it) }.forEach {
                val displayField = readProperty(it)
                var format = mentionConfig!!.mentionFormat
                selected.add(it)
                keys.forEach { key ->
                    format = format.replace("#$key;", readProperty(it, key))
                }
                parsedString = parsedString.replaceFirst("$trigger$displayField", format)
            }
            return parsedString
        }

        private fun getStartAndEndOfSubstring(str: String, sub: String): Pair<Int, Int> {
            val start = str.indexOf(sub)
            return when (start != -1) {
                true -> Pair(start, start + sub.length - 1)
                false -> Pair(-1, -1)
            }
        }

        interface IMentionHelper {
            fun onItemsCountChanged()
        }
    }
}