package motocitizen.ui.rows.message

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import motocitizen.content.message.Message
import motocitizen.main.R
import motocitizen.utils.name
import motocitizen.utils.timeString
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textView
import org.jetbrains.anko.wrapContent
//todo smell
abstract class MessageRow(context: Context, val message: Message, val type: Type) : FrameLayout(context) {
    enum class Type {
        FIRST, MIDDLE, LAST, ONE
    }

    //todo WTF!?
    abstract val ONE: Int
    abstract val FIRST: Int
    private val MIDDLE = R.drawable.message_row_middle
    private val LAST = R.drawable.message_row_last

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setBackground()
        joinRowsByOwner()
        textView(message.owner.name()) {
            layoutParams = LayoutParams(matchParent, wrapContent)
            visibility = if (type == Type.FIRST || type == Type.ONE) View.VISIBLE else View.INVISIBLE
            setTextColor(Color.parseColor(if (message.isOwner) "#00ffff" else "#ffff00"))
        }
        textView(String.format("%s%s \u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0", if (type == Type.MIDDLE || type == Type.LAST) "" else "\n", message.text)) {
            layoutParams = LayoutParams(wrapContent, matchParent)
            maxLines = 10
        }
        textView(message.time.timeString()) {
            layoutParams = LayoutParams(matchParent, wrapContent)
            gravity = Gravity.END or Gravity.BOTTOM
            setTextColor(Color.parseColor(if (message.isOwner) "#21272b" else "#21272b")) //todo 0xff21272b.toInt()
        }
    }

    private fun setBackground() = setBackgroundResource(
            when (type) {
                Type.MIDDLE -> MIDDLE
                Type.FIRST  -> FIRST
                Type.LAST   -> LAST
                Type.ONE    -> ONE
            })

    private fun joinRowsByOwner() {
        if (type == Type.MIDDLE || type == Type.LAST) {
            val lp = LinearLayout.LayoutParams(matchParent, wrapContent)
            lp.topMargin = 0
            layoutParams = lp
        }
    }
}