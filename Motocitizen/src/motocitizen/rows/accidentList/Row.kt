package motocitizen.rows.accidentList

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView

import motocitizen.accident.Accident
import motocitizen.main.R
import motocitizen.router.Router
import motocitizen.utils.MyUtils
import motocitizen.utils.popups.AccidentListPopup

@Suppress("LeakingThis")
abstract class Row : FrameLayout {

    protected constructor(context: Context, resourceId: Int, accident: Accident) : super(context) {
        LayoutInflater.from(context).inflate(resourceId, this, true)
        layoutParams = layoutParams()
        id = MyUtils.newId()
        if (accident.isHidden) {
            makeHidden()
        } else if (accident.isEnded) {
            makeEnded()
        } else {
            makeActive()
        }
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

    protected abstract fun makeActive()

    protected abstract fun makeHidden()

    protected abstract fun makeEnded()

    protected abstract fun layoutParams(): FrameLayout.LayoutParams

    protected fun makeActive(resourceId: Int) {
        setBackgroundResource(resourceId)
    }

    protected fun makeHidden(resourceId: Int) {
        setBackgroundResource(resourceId)
        (findViewById(R.id.accident_row_content) as TextView).setTextColor(0x30FFFFFF)
    }

    protected fun makeEnded(resourceId: Int) {
        setBackgroundResource(resourceId)
        (findViewById(R.id.accident_row_content) as TextView).setTextColor(0x70FFFFFF)
    }

    protected fun messagesText(accident: Accident): Spanned {
        val read = if (accident.unreadMessagesCount > 0) String.format("<font color=#C62828><b>(%s)</b></font>", accident.unreadMessagesCount) else ""
        val text = String.format("<b>%s</b>%s", accident.messages.size, read)
        if (Build.VERSION.SDK_INT >= 24) {
            return Html.fromHtml(text, android.text.Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE)
        } else {
            return Html.fromHtml(text)
        }
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
}
