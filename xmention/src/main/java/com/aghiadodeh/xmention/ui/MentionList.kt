package com.aghiadodeh.xmention.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aghiadodeh.xmention.R
import com.aghiadodeh.xmention.data.MentionConfig
import com.aghiadodeh.xmention.utils.MentionAdapter
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.ios.IosEmojiProvider

class MentionList(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    private lateinit var mentionAdapter: MentionAdapter<*, *>
    private var recyclerView: RecyclerView
    private var constraintLayout: ConstraintLayout

    init {
        EmojiManager.install(IosEmojiProvider())
        inflate(context, R.layout.mention_list, this)
        constraintLayout = findViewById(R.id.mention_constraintLayout)
        recyclerView = findViewById(R.id.mention_rv)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    fun setAdapter(mentionAdapter: MentionAdapter<*, *>) {
        this.mentionAdapter = mentionAdapter
        recyclerView.adapter = mentionAdapter
    }

    fun getAdapter(): MentionAdapter<*, *> = mentionAdapter

    fun getMentionData(): MentionConfig<*> = mentionAdapter.getMentionData()

    fun getRecyclerView(): RecyclerView = recyclerView

    fun getConstraintLayout(): ConstraintLayout = constraintLayout

    fun scrollToPosition(position: Int, animate: Boolean = false) {
        if (animate) recyclerView.smoothScrollToPosition(position)
        else recyclerView.scrollToPosition(position)
    }
}