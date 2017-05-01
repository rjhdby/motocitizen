package motocitizen.rows.message

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.FrameLayout.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout.LayoutParams.WRAP_CONTENT
import android.widget.TextView
import motocitizen.content.message.Message
import motocitizen.main.R
import motocitizen.utils.DateUtils

@SuppressLint("ViewConstructor")
open class MessageRow(context: Context, val message: Message, val last: Int, val next: Int) : FrameLayout(context) {
    open val SOLO = R.drawable.message_row
    open val FIRST = R.drawable.owner_message_row_first
    val MIDDLE = R.drawable.message_row_middle
    val LAST = R.drawable.message_row_last

    init {
        LayoutInflater.from(context).inflate(if (message.isOwner) R.layout.owner_message_row else R.layout.message_row, this, true)
        setBackgroundResource(background())
        if (last == message.owner.id) {
            val lp = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            lp.topMargin = 0
            layoutParams = lp
        }

        val ownerView = findViewById(R.id.owner) as TextView
        ownerView.text = message.owner.name
        if (message.owner.id == last) ownerView.visibility = View.INVISIBLE

        (findViewById(R.id.time) as TextView).text = DateUtils.getTime(message.time)
        //todo dirty hack
        (findViewById(R.id.text) as TextView).text = String.format("%s%s \u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0", if (message.owner.id == last) "" else "\n", message.text)
    }

    internal fun background(): Int {
        return when {
            next == last && message.owner.id == next -> MIDDLE
            message.owner.id == next                 -> FIRST
            message.owner.id == last                 -> LAST
            else                                     -> SOLO
        }
    }
}
