package motocitizen.ui.rows.accident

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
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
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textView
import org.jetbrains.anko.wrapContent

//todo refactor
abstract class Row protected constructor(context: Context, val accident: Accident) : FrameLayout(context) {
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
        return if (Build.VERSION.SDK_INT >= 24) {
            Html.fromHtml(text, android.text.Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE)
        } else {
            @Suppress("DEPRECATION")
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
        textView(accident.time.getIntervalFromNowInText()) {
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
        layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent).margins(margins)
    }

    private fun setUpListeners() {
        setOnClickListener { clickListener() }
        setOnLongClickListener { longClickListener(it) }
    }

    private fun clickListener() {
        (context as Activity).goTo(Screens.DETAILS, mapOf(AccidentDetailsActivity.ACCIDENT_ID_KEY to accident.id))
    }

    private fun longClickListener(v: View): Boolean {
        AccidentContextMenu(context, accident).showAsDropDown(v)
        return true
    }
}