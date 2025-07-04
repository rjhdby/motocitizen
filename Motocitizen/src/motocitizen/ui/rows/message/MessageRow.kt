package motocitizen.ui.rows.message

import android.content.Context
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import motocitizen.content.message.Message
import motocitizen.main.R
import motocitizen.utils.name
import motocitizen.utils.timeString
import androidx.core.graphics.toColorInt

//todo smell
abstract class MessageRow(context: Context, val message: Message, val type: Type) :
    FrameLayout(context) {
    enum class Type {
        FIRST, MIDDLE, LAST, ONE
    }

    //todo WTF!?
    abstract val ONE: Int
    abstract val FIRST: Int
    private val MIDDLE = R.drawable.message_row_middle
    private val LAST = R.drawable.message_row_last

    private val defaultLayoutParams = LayoutParams(
        LayoutParams.MATCH_PARENT,
        LayoutParams.WRAP_CONTENT
    )

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setBackground()
        joinRowsByOwner()
        addView(textView(message.owner.name())).apply {
            layoutParams = defaultLayoutParams
        }
        addView(textView(message.owner.name()).apply {
            setTextColor((if (message.isOwner) "#00ffff" else "#ffff00").toColorInt())
        }).apply {
            visibility = if (type == Type.FIRST || type == Type.ONE) VISIBLE else INVISIBLE
        }
        addView(
            textView(
                String.format(
                    "%s%s \u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0",
                    if (type == Type.MIDDLE || type == Type.LAST) "" else "\n",
                    message.text
                )
            ).apply {
                maxLines = 10
            }
        ).apply {
            layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.MATCH_PARENT,
            )
        }
        addView(textView(message.time.timeString()).apply {
            setTextColor((if (message.isOwner) "#21272b" else "#21272b").toColorInt()) //todo 0xff21272b.toInt()
            gravity = Gravity.END or Gravity.BOTTOM
        }).apply {
            layoutParams = defaultLayoutParams
        }
    }

    private fun textView(text: String): TextView = TextView(context).apply {
        this.text = text
    }

    private fun setBackground() = setBackgroundResource(
        when (type) {
            Type.MIDDLE -> MIDDLE
            Type.FIRST -> FIRST
            Type.LAST -> LAST
            Type.ONE -> ONE
        }
    )

    private fun joinRowsByOwner() {
        if (type != Type.MIDDLE && type != Type.LAST) return
        val lp = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        lp.topMargin = 0
        layoutParams = lp
    }
}