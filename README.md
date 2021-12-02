# Android Vertical Stepper

### Implementation
add `maven { url 'https://jitpack.io' }` to build.gradle in your project:
``` groovy
allprojects {
    repositories {
		...
        maven { url 'https://jitpack.io' }
    }
}
```
and in your app.gradle:
``` groovy
dependencies {
	...
	implementation 'com.github.aghiadodeh:xmention:1.0.0'
}
```


### Overview
![](https://s8.gifyu.com/images/20211202_011936.gif)

## 

### Usage:

#### Stepper

Attribute Name | Format | Default Value | Description
------------- | ------------- | -------------
input_trigger | string | @ | charachter which open mention list
input_hint_text  | string | "" | mention input hint
input_tint_color  | color | android.R.color.transparent | editText underline color
input_show_emoji_icon | boolean | true | emoji icon visibility
input_text_size  | dimension | 18sp | mention input text size
input_highlight_color  | color | android.R.color.holo_blue_dark | color of highlighted word in mention input
input_emoji_icon | reference | R.drawable.ic_emoji | mention input emoji icon (drawable)
input_keyboard_icon  | reference | R.drawable.ic_keyboard | mention input keyboard icon (drawable)


#### Methods

`textInputLayout(): TextInputLayout`: Parent of input editText

`editText(): EditText`: Iinput editText

`originalText(): String`: Displayed Text in EditText, example: ```@Annette Cooper```

`parsedString(): String`: Parsed Text, example: ```<mention>34154f50-59b5-4863-acd5-d6dd40dabd8e,Annette Cooper</mention>```

`appendTrigger()`: Add trigger charachter at the end of input text
## 

in `your_activity.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <com.aghiadodeh.xmention.ui.MentionList
        android:id="@+id/mention_list"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:layout_constraintBottom_toTopOf="@id/mention_input"/>

    <com.aghiadodeh.xmention.ui.MentionInput
        android:id="@+id/mention_input"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:layout_constraintBottom_toTopOf="@id/mention_helper"
        app:input_trigger="@"
        app:input_hint_text="Type a name..."
        app:input_tint_color="@color/white"
        app:input_text_size="@dimen/small_text"
        app:input_highlight_color="@color/purple_500"
        app:input_emoji_icon="@drawable/ic_emoji"/>

</androidx.constraintlayout.widget.ConstraintLayout>
```

Create your Data Model:
```kotlin
// make sure you data model has `id` key
data class User(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val email: String
)

```

Mention Configration:

```kotlin
val mentionData = MentionConfig(
	data = users, // ArrayList<User>
	matchFields = listOf("name", "email"), // find user if (name or email) contains input text after trigger
	displayField = "name",
	maxSuggestions = 4,
	mentionFormat = "<mention>#id;,#name;</mention>" // used when call `mentionInput.parsedString()` method
)
```

Mention List Adapter which extends `MentionAdapter`:

```kotlin
class MentionUsersAdapter(mentionConfig: MentionConfig<User>) : MentionAdapter<User, MentionUsersAdapter.ViewHolder>(mentionConfig) {
    private var query: String = ""
    private val data = getMentionData().mutableList

    override fun createHolder(): ViewHolder { // instead of `onCreateViewHolder`
        val layoutView = LayoutInflater.from(parent.context).inflate(R.layout.your_xml, parent, false)
        return ViewHolder(layoutView)
    }

    override fun bindHolder(viewHolder: ViewHolder, position: Int) {  // instead of `onBindViewHolder`
        val user = data[position] as User
        // ...
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun watchQuery(query: String) {
     	// if you want to detect mention input query after trigger
        this.query = query
        notifyDataSetChanged()
    }

    class ViewHolder(layoutView: View) : RecyclerView.ViewHolder(layoutView) {
        // ...
    }
}
```

in `YourActivity.kt`:
```kotlin

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
            data = users,
            matchFields = listOf("name", "email"),
            displayField = "name",
            maxSuggestions = 4,
            mentionFormat = "<mention>#id;,#name;</mention>" // used when call `mentionInput.parsedString()` method
        )

        // init adapter
        val mentionAdapter = MentionUsersAdapter(mentionData)
        mentionList.setAdapter(mentionAdapter)

        // IMPORTANT: link input with list
        mentionInput.attachToMentionList(mentionList)

        // open mentionList by add trigger
        findViewById<MaterialButton>(R.id.trigger).setOnClickListener {
            mentionInput.appendTrigger()
        }

        // print result
        findViewById<MaterialButton>(R.id.submit).setOnClickListener {
            Log.d("parsedString", mentionInput.parsedString())
            Log.d("originalText", mentionInput.originalText())
        }
    }
}
```
