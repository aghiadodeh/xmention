package com.aghiadodehgithub.mention

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.aghiadodeh.xmention.data.MentionConfig
import com.aghiadodeh.xmention.ui.MentionInput
import com.aghiadodeh.xmention.ui.MentionList
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    private val users: ArrayList<User> by lazy { DataHelper.generateUsers() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // init views
        val mentionInput: MentionInput<User> = findViewById(R.id.mention_input)
        val mentionList: MentionList = findViewById(R.id.mention_list)

        // init mention configuration
        val mentionData = MentionConfig(
            data = users, // ArrayList<User>
            matchFields = listOf("name", "email"), // find user if user's (name or email) contains input text after trigger
            displayField = "name",
            maxSuggestions = 4,
            mentionFormat = "<mention>#id;,#name;</mention>" // used when call `mentionInput.parsedString()` method
        )

        // init adapter
        val mentionAdapter = MentionUsersAdapter(mentionData)
        mentionList.setAdapter(mentionAdapter = mentionAdapter)

        // IMPORTANT: link input with list
        mentionInput.attachToMentionList(mentionList = mentionList)

        // open mentionList by add trigger
        findViewById<MaterialButton>(R.id.trigger).setOnClickListener {
            mentionInput.appendTrigger()
        }

        // print result
        findViewById<MaterialButton>(R.id.submit).setOnClickListener {
            Log.e("parsedString", mentionInput.parsedString())
            Log.e("originalText", mentionInput.originalText())
        }
    }
}


/**
 * make sure you data model has `id` key
 */
data class User(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val email: String,
    val image: String
)

