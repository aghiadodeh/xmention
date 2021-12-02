package com.aghiadodeh.xmention.utils

import androidx.recyclerview.widget.DiffUtil
import com.aghiadodeh.xmention.utils.MentionUtils

class DiffCallBack(private val oldList: List<*>, private val newList: List<*>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return try {
            val oldItemId = MentionUtils.readInstanceProperty<String>(oldList[oldItemPosition] as Any, "id")
            val newItemId = MentionUtils.readInstanceProperty<String>(newList[newItemPosition] as Any, "id")
            oldItemId == newItemId
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
            false
        }
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = MentionUtils.readInstanceProperty<String>(oldList[oldItemPosition] as Any, "id")
        val newItem = MentionUtils.readInstanceProperty<String>(newList[newItemPosition] as Any, "id")
        return oldItem == newItem
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }
}