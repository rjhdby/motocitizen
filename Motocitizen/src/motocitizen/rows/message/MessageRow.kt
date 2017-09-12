package motocitizen.rows.message

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import motocitizen.content.Content
import motocitizen.content.message.Message
import motocitizen.main.R
import motocitizen.utils.getTime

abstract class MessageRow(context: Context, val message: Message, private val last: Int, private val next: Int) : FrameLayout(context) {
    abstract val LAYOUT: Int
    abstract val SOLO: Int
    abstract val FIRST: Int
    private val MIDDLE = R.drawable.message_row_middle
    private val LAST = R.drawable.message_row_last

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        LayoutInflater.from(context).inflate(LAYOUT, this, true)
        setBackground()
        joinRowsByOwner()
        setUpOwnerField()
        bindValues()
    }

    private fun setBackground() {
        setBackgroundResource(
                when {
                    next == last && message.owner == next -> MIDDLE
                    message.owner == next                 -> FIRST
                    message.owner == last                 -> LAST
                    else                                  -> SOLO
                })
    }

    private fun joinRowsByOwner() {
        if (last == message.owner) {
            val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
            lp.topMargin = 0
            layoutParams = lp
        }
    }

    private fun bindValues() {
        (findViewById(R.id.time) as TextView).text = getTime(message.time)
        //todo dirty hack
        (findViewById(R.id.text) as TextView).text = String.format("%s%s \u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0", if (message.owner == last) "" else "\n", message.text)
    }

    private fun setUpOwnerField() {
        val ownerView = findViewById(R.id.owner) as TextView
        ownerView.text = Content.volunteers[message.owner]!!.name
        if (message.owner == last) ownerView.visibility = View.INVISIBLE
    }
}