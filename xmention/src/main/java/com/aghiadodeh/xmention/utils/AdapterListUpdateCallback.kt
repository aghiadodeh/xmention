package com.aghiadodeh.xmention.utils

import androidx.recyclerview.widget.ListUpdateCallback
import com.aghiadodeh.xmention.utils.MentionAdapter

class AdapterListUpdateCallback(private val adapter: MentionAdapter<*, *>) : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {
        adapter.notifyItemRangeInserted(position, count)
    }

    override fun onRemoved(position: Int, count: Int) {
        adapter.notifyItemRangeRemoved(position, count)
    }

    override fun onMoved(fromPosition: Int, toPosition: Int) {
        adapter.notifyItemMoved(fromPosition, toPosition)
    }

    override fun onChanged(position: Int, count: Int, payload: Any?) {
        adapter.notifyItemRangeChanged(position, count, payload)
    }
}