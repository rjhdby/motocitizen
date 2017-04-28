package motocitizen.rows.accidentList

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView

import motocitizen.content.accident.Accident
import motocitizen.main.R
import motocitizen.router.Router
import motocitizen.utils.MyUtils
import motocitizen.utils.popups.AccidentListPopup

abstract class Row protected constructor(context: Context, accident: Accident) : FrameLayout(context) {
    val ACTIVE_COLOR = 0x70FFFFFF
    val ENDED_COLOR = 0x70FFFFFF
    val HIDDEN_COLOR = 0x30FFFFFF
    abstract val background: Int
    abstract val layout: Int
    abstract val textColor: Int
    val mLayoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

    abstract fun changeMargins()

    protected fun messagesText(accident: Accident): Spanned {
        val read = if (accident.unreadMessagesCount > 0) String.format("<font color=#C62828><b>(%s)</b></font>", accident.unreadMessagesCount) else ""
        val text = String.format("<b>%s</b>%s", accident.messages.size, read)
        if (Build.VERSION.SDK_INT >= 24) {
            return Html.fromHtml(text, android.text.Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE)
        } else {
            return Html.fromHtml(text)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        changeMargins()
        layoutParams = mLayoutParams
    }

    init {
        LayoutInflater.from(context).inflate(layout, this, true)
        id = MyUtils.newId()
        setBackgroundResource(background)
        (findViewById(R.id.accident_row_content) as TextView).setTextColor(textColor)
        (findViewById(R.id.accident_row_content) as TextView).text = context.resources.getString(R.string.accident_row_content, accident.title())
        (findViewById(R.id.accident_row_time) as TextView).text = MyUtils.getIntervalFromNowInText(context, accident.time)
        (findViewById(R.id.accident_row_unread) as TextView).text = messagesText(accident)
        setOnClickListener { _ ->
            val bundle = Bundle()
            bundle.putInt("accidentID", accident.id)
            Router.goTo(context as Activity, Router.Target.DETAILS, bundle)
        }
        setOnLongClickListener { v ->
            val viewLocation = IntArray(2)
            v.getLocationOnScreen(viewLocation)
            AccidentListPopup(context, accident.id)
                    .getPopupWindow(context)
                    .showAtLocation(v, Gravity.NO_GRAVITY, viewLocation[0], viewLocation[1])
            true
        }
    }
}
