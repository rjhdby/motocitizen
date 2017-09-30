package motocitizen.ui.rows.accident

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import motocitizen.content.accident.Accident
import motocitizen.datasources.database.StoreMessages
import motocitizen.main.R
import motocitizen.router.Router
import motocitizen.ui.activity.AccidentDetailsActivity
import motocitizen.ui.popups.AccidentListPopup
import motocitizen.utils.getIntervalFromNowInText
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textView
import org.jetbrains.anko.wrapContent

//todo refactor
abstract class Row protected constructor(context: Context, val accident: Accident) : FrameLayout(context) {
    val ACTIVE_COLOR = 0x70FFFFFF
    val ENDED_COLOR = 0x70FFFFFF
    val HIDDEN_COLOR = 0x30FFFFFF
    abstract val background: Int
    abstract val textColor: Int
    abstract val margins: Array<Int>

    private fun messagesText(accident: Accident): Spanned {
        val text = formatMessagesText(accident)
        return if (Build.VERSION.SDK_INT >= 24) {
            Html.fromHtml(text, android.text.Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE)
        } else {
            Html.fromHtml(text)
        }
    }

    //todo remove html
    private fun formatMessagesText(accident: Accident): String {
        if (accident.messagesCount == 0) return ""
        val read = StoreMessages.getLast(accident.id)
        return if (accident.messagesCount > read)
            String.format("<font color=#C62828><b>(%s)</b></font>", accident.messagesCount)
        else
            String.format("<b>%s</b>", accident.messagesCount)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setMargins()
        setBackgroundResource(background)
        textView(context.resources.getString(R.string.accident_row_content, accident.title())) {
            layoutParams = LayoutParams(matchParent, wrapContent)
            minLines = 3
            setTextColor(textColor)
        }
        textView(getIntervalFromNowInText(accident.time)) {
            layoutParams = LayoutParams(matchParent, matchParent)
            gravity = Gravity.END
            typeface = Typeface.DEFAULT_BOLD
        }
        textView(messagesText(accident)) {
            layoutParams = LayoutParams(matchParent, matchParent)
            gravity = Gravity.BOTTOM or Gravity.END
        }
        setUpListeners()
    }

    private fun setMargins() {
        layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
                .apply { setMargins(margins[0], margins[1], margins[2], margins[3]) }
    }

    //todo extract
    private fun setUpListeners() {
        setOnClickListener { clickListener() }
        setOnLongClickListener { longClickListener(it) }
    }

    private fun clickListener() {
        val bundle = Bundle()
        bundle.putInt(AccidentDetailsActivity.ACCIDENT_ID_KEY, accident.id)
        Router.goTo(context as Activity, Router.Target.DETAILS, bundle)
    }

    private fun longClickListener(v: View): Boolean {
        val viewLocation = IntArray(2)
        v.getLocationOnScreen(viewLocation)
        AccidentListPopup(context, accident)
                .showAtLocation(v, Gravity.NO_GRAVITY, viewLocation[0], viewLocation[1])
        return true
    }
}
