package com.aghiadodehgithub.mention

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aghiadodeh.xmention.data.MentionConfig
import com.aghiadodeh.xmention.utils.MentionAdapter

class MentionUsersAdapter(mentionConfig: MentionConfig<User>) : MentionAdapter<User, MentionUsersAdapter.ViewHolder>(mentionConfig) {
    private var query: String = ""
    private val data = getMentionData().mutableList

    override fun createHolder(): ViewHolder {
        val layoutView = LayoutInflater.from(parent.context).inflate(R.layout.card_user_mention, parent, false)
        return ViewHolder(layoutView)
    }

    override fun bindHolder(viewHolder: ViewHolder, position: Int) {
        val user = data[position] as User
        SpanUtils.styleWord(textView = viewHolder.username, searchText = arrayListOf(query), searchWords = arrayListOf(user.name))
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun watchQuery(query: String) {
        this.query = query
        notifyDataSetChanged()
    }

    class ViewHolder(layoutView: View) : RecyclerView.ViewHolder(layoutView) {
        val username: TextView = layoutView.findViewById(R.id.card_user_mention_username)
    }
}