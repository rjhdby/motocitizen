package motocitizen.ui.rows.accident

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.text.Html
import android.text.Spanned
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import motocitizen.content.accident.Accident
import motocitizen.datasources.database.StoreMessages
import motocitizen.main.R
import motocitizen.ui.Screens
import motocitizen.ui.activity.AccidentDetailsActivity
import motocitizen.ui.menus.AccidentContextMenu
import motocitizen.utils.Margins
import motocitizen.utils.getIntervalFromNowInText
import motocitizen.utils.goTo
import motocitizen.utils.margins

//todo refactor
abstract class Row protected constructor(context: Context, val accident: Accident) :
    FrameLayout(context) {
    companion object {
        const val ACTIVE_COLOR = 0x70FFFFFF
        const val ENDED_COLOR = 0x70FFFFFF
        const val HIDDEN_COLOR = 0x30FFFFFF
    }

    abstract val background: Int
    abstract val textColor: Int
    abstract val margins: Margins

    private fun messagesText(accident: Accident): Spanned {
        val text = formatMessagesText(accident)
        return Html.fromHtml(text, Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE)
    }

    //todo remove html
    private fun formatMessagesText(accident: Accident): String {
        if (accident.messagesCount == 0) return ""
        val read = StoreMessages.getLast(accident.id)
        return when {
            accident.messagesCount > read -> "<font color=#C62828><b>(%s)</b></font>"
            else -> "<b>%s</b>"
        }.also {
            String.format(it, accident.messagesCount)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setMargins()
        setBackgroundResource(background)
        textView(context.resources.getString(R.string.accident_row_content, accident.title()))
            .apply {
                layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                minLines = 3
                setTextColor(textColor)
            }.also { addView(it) }
        textView(accident.time.getIntervalFromNowInText()).apply {
            layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
            gravity = Gravity.END
            typeface = Typeface.DEFAULT_BOLD
        }.also { addView(it) }
        textView(messagesText(accident)).apply {
            layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
            gravity = Gravity.BOTTOM or Gravity.END
        }.also { addView(it) }
        setUpListeners()
    }

    private fun textView(text: String): TextView = TextView(context).apply {
        this.text = text
    }

    private fun textView(text: Spanned): TextView = TextView(context).apply {
        this.text = text
    }

    private fun setMargins() {
        layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).margins(margins)
    }

    private fun setUpListeners() {
        setOnClickListener { clickListener() }
        setOnLongClickListener { longClickListener(it) }
    }

    private fun clickListener() {
        (context as Activity).goTo(
            Screens.DETAILS,
            mapOf(AccidentDetailsActivity.ACCIDENT_ID_KEY to accident.id)
        )
    }

    private fun longClickListener(v: View): Boolean {
        AccidentContextMenu(context, accident).showAsDropDown(v)
        return true
    }
}