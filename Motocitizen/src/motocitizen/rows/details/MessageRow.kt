package motocitizen.rows.details

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.FrameLayout.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout.LayoutParams.WRAP_CONTENT
import android.widget.TextView
import motocitizen.accident.Message
import motocitizen.main.R
import motocitizen.utils.DateUtils

@SuppressLint("ViewConstructor")
class MessageRow(context: Context, val message: Message, val last: Int, val next: Int) : FrameLayout(context) {

    init {
        LayoutInflater.from(context).inflate(if (message.isOwner) R.layout.owner_message_row else R.layout.message_row, this, true)
        setBackgroundResource(background())
        if (last == message.ownerId) {
            val lp = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            lp.topMargin = 0
            layoutParams = lp
        }

        val ownerView = findViewById(R.id.owner) as TextView
        ownerView.text = message.owner
        if (message.ownerId == last) ownerView.visibility = View.INVISIBLE

        (findViewById(R.id.time) as TextView).text = DateUtils.getTime(message.time)
        //todo dirty hack
        (findViewById(R.id.text) as TextView).text = String.format("%s%s \u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0", if (message.ownerId == last) "" else "\n", message.text)
    }

    private fun background(): Int {
        return when {
            message.ownerId == last && message.ownerId == next -> R.drawable.message_row_middle
            message.ownerId == next && message.isOwner         -> R.drawable.owner_message_row_first
            next == message.ownerId                            -> R.drawable.message_row_first
            last == message.ownerId                            -> R.drawable.message_row_last
            message.isOwner                                    -> R.drawable.owner_message_row
            else                                               -> R.drawable.message_row
        }
    }
}
