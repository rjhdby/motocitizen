package motocitizen.ui.rows.message

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import motocitizen.content.Content
import motocitizen.content.message.Message
import motocitizen.main.R
import motocitizen.utils.getTime

abstract class MessageRow(context: Context, val message: Message, val type: Type) : FrameLayout(context) {
    companion object {
        enum class Type {
            FIRST, MIDDLE, LAST, ONE
        }
    }

    abstract val LAYOUT: Int
    abstract val ONE: Int
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
                when (type) {
                    Type.MIDDLE -> MIDDLE
                    Type.FIRST  -> FIRST
                    Type.LAST   -> LAST
                    Type.ONE    -> ONE
                })
    }

    private fun joinRowsByOwner() {
        if (type == Type.MIDDLE || type == Type.LAST) {
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            lp.topMargin = 0
            layoutParams = lp
        }
    }

    private fun bindValues() {
        (findViewById(R.id.time) as TextView).text = getTime(message.time)
        //todo dirty hack
        (findViewById(R.id.text) as TextView).text = String.format("%s%s \u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0", if (type == Type.MIDDLE || type == Type.LAST) "" else "\n", message.text)
    }

    private fun setUpOwnerField() {
        val ownerView = findViewById(R.id.owner) as TextView
        ownerView.text = message.ownerName()
        if (type == Type.MIDDLE || type == Type.LAST) ownerView.visibility = View.INVISIBLE
    }
}