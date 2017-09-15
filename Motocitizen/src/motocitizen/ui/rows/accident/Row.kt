package motocitizen.ui.rows.accident

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
import motocitizen.datasources.database.StoreMessages
import motocitizen.main.R
import motocitizen.router.Router
import motocitizen.ui.activity.AccidentDetailsActivity
import motocitizen.utils.getIntervalFromNowInText
import motocitizen.utils.newId
import motocitizen.utils.popups.AccidentListPopup

//todo refactor
abstract class Row protected constructor(context: Context, val accident: Accident) : FrameLayout(context) {
    val ACTIVE_COLOR = 0x70FFFFFF
    val ENDED_COLOR = 0x70FFFFFF
    val HIDDEN_COLOR = 0x30FFFFFF
    abstract val background: Int
    abstract val LAYOUT: Int
    abstract val textColor: Int
    abstract val margins: Array<Int>

    init {
        id = newId()
    }

    //todo messages
    private fun messagesText(accident: Accident): Spanned {
        val text = formatMessagesText(accident)
        return if (Build.VERSION.SDK_INT >= 24) {
            Html.fromHtml(text, android.text.Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE)
        } else {
            Html.fromHtml(text)
        }
    }

    private fun formatMessagesText(accident: Accident): String {
        if (accident.messagesCount() == 0) return ""
        val read = StoreMessages.getLast(accident.id)
        return if (accident.messagesCount() > read)
            String.format("<font color=#C62828><b>(%s)</b></font>", accident.messagesCount())
        else
            String.format("<b>%s</b>", accident.messagesCount())
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        lp.setMargins(margins[0], margins[1], margins[2], margins[3])
        layoutParams = lp
        LayoutInflater.from(context).inflate(LAYOUT, this, true)
        setBackgroundResource(background)
        bindValues()
        setUpListeners()
    }

    private fun bindValues() {
        (findViewById(R.id.accident_row_content) as TextView).setTextColor(textColor)
        (findViewById(R.id.accident_row_content) as TextView).text = context.resources.getString(R.string.accident_row_content, accident.title())
        (findViewById(R.id.accident_row_time) as TextView).text = getIntervalFromNowInText(context, accident.time)
        (findViewById(R.id.accident_row_unread) as TextView).text = messagesText(accident)
    }

    private fun setUpListeners() {
        setOnClickListener { _ ->
            val bundle = Bundle()
            bundle.putInt(AccidentDetailsActivity.ACCIDENT_ID_KEY, accident.id)
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
