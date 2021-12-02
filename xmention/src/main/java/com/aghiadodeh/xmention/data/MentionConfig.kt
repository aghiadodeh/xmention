package com.aghiadodeh.xmention.data

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.aghiadodeh.xmention.interfaces.IQuery
import com.aghiadodeh.xmention.utils.AdapterListUpdateCallback
import com.aghiadodeh.xmention.utils.DiffCallBack
import com.aghiadodeh.xmention.utils.MentionAdapter
import com.aghiadodeh.xmention.utils.MentionUtils
import com.google.gson.Gson
import io.reactivex.Completable
import java.util.*
import kotlin.math.min

data class MentionConfig<T>(
    val data: List<T> = listOf(),
    val matchFields: List<String>,
    val displayField: String,
    val mentionFormat: String = "<mention>#id;,#$displayField;</mention>",
    val maxSuggestions: Int = 5
) {
    private val tag = "MentionConfig"
    private var originalList: MutableList<T> = mutableListOf()
    private var filteredList: MutableList<T> = mutableListOf()
    private var iQuery: IQuery? = null
    var mutableList: MutableList<T> = mutableListOf()

    init {
        originalList = data.toMutableList()
        mutableList.addAll(data)
    }

    fun search(query: String, currentItems: MutableList<*> = mutableListOf<T>(), selectionEnd: Int): Completable = Completable.create {
        // if (query.isEmpty()) return@create
        // Log.e(TAG, "query: $query")
        val wanted = originalList.filter { item ->
            var getItem = false
            matchFields.forEach { matchField ->
                val value = MentionUtils.readInstanceProperty<String>(item as Any, matchField)
                if (contains(value, query, selectionEnd)) getItem = true
            }
            getItem
        }
        filteredList.clear()
        filteredList.addAll(wanted.filter { item -> !currentItems.contains(item) })
        filteredList.sortBy { item -> getField(item, displayField).lowercase(Locale.getDefault()).indexOf(query.lowercase(Locale.getDefault())) }
        // Log.e(TAG, "size: ${filteredList.size}")
        it.onComplete()
    }

    private fun contains(value: String, query: String, selectionEnd: Int): Boolean {
        val split = query.split(" ")
        val mValue = value.lowerCase()
        val mQuery = query.lowerCase()
        val subQuery = if (split.isNotEmpty()) split.first().lowercase() else mQuery
        //Log.e(tag, "query $query, subQuery $subQuery")
        if (mValue.contains(mQuery) && mValue.length >= mQuery.length) {
            return true
        } else if (split.size > 1) {
            val newQuery = "$subQuery ${split[1].lowercase()}"//.trim()
            return when {
                mValue.contains(mQuery) -> {
                    // Log.e(tag, "mValue $mValue, mQuery $mQuery, newQuery $newQuery,")
                    true
                }
                (mValue.contains(subQuery) && !mValue.contains(newQuery) && mValue.split(" ").size > 1) -> {
                    // Log.e(tag, "mValue $mValue, mQuery $mQuery, newQuery $newQuery, size ${mValue.split(" ").size}")
                    true
                }
                split.size == 1 -> {
                    Log.e(tag, "subQuery ${subQuery}, value $mValue,")
                    mValue.contains(subQuery)
                }
                else -> false
            }
        }
        return false
    }

    fun notifyDataSetChanged(recyclerView: RecyclerView, query: String) {
        val diffResult = DiffUtil.calculateDiff(DiffCallBack(mutableList, filteredList))
        mutableList.clear()
        mutableList.addAll(filteredList)
        diffResult.dispatchUpdatesTo(AdapterListUpdateCallback(adapter = recyclerView.adapter as MentionAdapter<*, *>))
        iQuery?.textChanges(query)
        fixRecyclerViewHeight(recyclerView)
    }

    private fun fixRecyclerViewHeight(recyclerView: RecyclerView) {
        val adapter = recyclerView.adapter as MentionAdapter<*, *>
        val count = min(adapter.itemCount, maxSuggestions)
        var height: Int
        //  || adapter.getMentionData().filteredList.size == adapter.getMentionData().data.size
        if (adapter.itemCount == 0) {
            // avoid IndexOutOfBoundsException
            recyclerView.visibility = View.GONE
            return
        } else {
            recyclerView.visibility = View.VISIBLE
            recyclerView.post {
                adapter.view?.let {
                    val itemHeight = it.height
                    height = count * (itemHeight + 20)
                    changeViewSize(recyclerView, height)
                }
            }
        }
    }

    private fun changeViewSize(view: View, newHeight: Int) {
        val valueAnimator = ValueAnimator.ofInt(view.measuredHeight, newHeight)
        valueAnimator.duration = 500L
        valueAnimator.addUpdateListener {
            val animatedValue = valueAnimator.animatedValue as Int
            val layoutParams = view.layoutParams
            layoutParams.height = animatedValue
            view.layoutParams = layoutParams
        }
        valueAnimator.start()
        valueAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {

            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onAnimationEnd(animation: Animator?) {
                if (view is RecyclerView) {
                    view.adapter?.notifyDataSetChanged() // fix viewHolder position
                    view.smoothScrollToPosition(0)
                }
            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationRepeat(animation: Animator?) {

            }
        })
    }

    private fun gsonToJson(gson: List<T>): String? {
        return Gson().toJson(gson)
    }

    fun setIQuery(iQuery: IQuery) {
        this.iQuery = iQuery
    }

    fun queryComparator(query: String): Comparator<T> = Comparator { o1, o2 ->
        val string = query.lowercase(Locale.getDefault())
        val nameO1 = getField(o1, displayField).lowercase(Locale.getDefault())
        val nameO2 = getField(o2, displayField).lowercase(Locale.getDefault())
        when {
            nameO1.indexOf(string) < nameO2.indexOf(string) -> 0
            nameO1.indexOf(string) > nameO2.indexOf(string) -> 1
            else -> 0
        }
    }

    private fun getField(item: T, field: String): String {
        return MentionUtils.readInstanceProperty<String>(item as Any, field)
    }

    private fun String.lowerCase(): String = this.lowercase(Locale.getDefault())//.replace(" ", "")

    data class SearchData(val found: Boolean, val display: Boolean)
}
