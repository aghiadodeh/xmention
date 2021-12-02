package com.aghiadodeh.xmention.utils

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aghiadodeh.xmention.data.MentionConfig
import com.aghiadodeh.xmention.interfaces.IMention

abstract class MentionAdapter<T, VB : RecyclerView.ViewHolder>(private var mentionConfig: MentionConfig<T>) : RecyclerView.Adapter<VB>() {
    var view: View? = null
    lateinit var parent: ViewGroup
    private var iMention: IMention? = null

    abstract fun createHolder(): VB
    abstract fun bindHolder(viewHolder: VB, position: Int)
    abstract fun watchQuery(query: String)

    init {
        mentionConfig.setIQuery{ watchQuery(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VB {
        this.parent = parent
        return createHolder().apply { view = itemView }
    }

    override fun onBindViewHolder(viewHolder: VB, position: Int) {
        bindHolder(viewHolder, position)
        viewHolder.itemView.setOnClickListener {
            iMention?.onSelect(item = mentionConfig.mutableList[position] as Any)
        }
    }

    override fun getItemCount(): Int = getMentionData().mutableList.size

    fun getMentionData(): MentionConfig<*> = mentionConfig

    fun setListener(iMention: IMention) {
        this.iMention = iMention
    }
}