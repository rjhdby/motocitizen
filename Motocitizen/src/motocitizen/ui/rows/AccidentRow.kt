package motocitizen.ui.rows

import android.content.Context
import android.graphics.Typeface
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.ConstraintSet.WRAP_CONTENT
import androidx.core.text.HtmlCompat
import motocitizen.content.accident.Accident
import motocitizen.dictionary.AccidentStatus
import motocitizen.main.R
import motocitizen.ui.menus.AccidentContextMenu
import motocitizen.utils.Margins
import motocitizen.utils.getIntervalFromNowInText

class AccidentRow private constructor(
    context: Context,
    private val accident: Accident,
    val preset: Preset,
) : ConstraintLayout(context) {
    companion object {
        fun make(context: Context, accident: Accident): AccidentRow = when (accident.status) {
            AccidentStatus.ACTIVE -> if (accident.isOwner()) Preset.OwnedActive else Preset.CommonActive
            AccidentStatus.HIDDEN -> if (accident.isOwner()) Preset.OwnedHidden else Preset.CommonHidden
            AccidentStatus.ENDED -> if (accident.isOwner()) Preset.OwnedEnded else Preset.CommonEnded
        }.let { AccidentRow(context, accident, it) }

        const val ACTIVE_COLOR = 0x70FFFFFF
        const val ENDED_COLOR = 0x70FFFFFF
        const val HIDDEN_COLOR = 0x30FFFFFF

        val COMMON_ROW_MARGINS = Margins(left = 4, top = 2, right = 16, bottom = 2)
        val OWNED_ROW_MARGINS = Margins(left = 16, top = 2, right = 4, bottom = 2)
    }

    enum class Preset(val textColor: Int, val background: Int, val margins: Margins) {
        CommonActive(ACTIVE_COLOR, R.drawable.message_row, COMMON_ROW_MARGINS),
        CommonHidden(HIDDEN_COLOR, R.drawable.accident_row_hidden, COMMON_ROW_MARGINS),
        CommonEnded(ENDED_COLOR, R.drawable.accident_row_ended, COMMON_ROW_MARGINS),

        OwnedActive(ACTIVE_COLOR, R.drawable.owner_message_row, OWNED_ROW_MARGINS),
        OwnedHidden(HIDDEN_COLOR, R.drawable.owner_accident_hidden, OWNED_ROW_MARGINS),
        OwnedEnded(ENDED_COLOR, R.drawable.owner_accident_ended, OWNED_ROW_MARGINS),
        ;
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setBackgroundResource(preset.background)

        val header = TextView(context).apply {
            id = generateViewId()
            layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            setTextColor(preset.textColor)
            text = accident.header()
        }

        val timer = TextView(context).apply {
            id = generateViewId()
            layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            text = accident.time.getIntervalFromNowInText()
            setTextColor(preset.textColor)
            typeface = Typeface.DEFAULT_BOLD
        }

        val body = TextView(context).apply {
            id = generateViewId()
            layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            setTextColor(preset.textColor)
            text = accident.body()
        }

        val yandexView = accident.extractYandexUrl()?.let {
            TextView(context).apply {
                id = generateViewId()
                layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
                setTextColor(preset.textColor)
                text = HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_LEGACY)
                movementMethod = LinkMovementMethod.getInstance()
                linksClickable = true
                isClickable = false
                isFocusable = false
            }
        }

        this.apply {
            addView(header)
            addView(timer)
            addView(body)
            if (yandexView != null) addView(yandexView)
        }

        ConstraintSet().apply {
            clone(this@AccidentRow)
            connect(header.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 4)
            connect(header.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 4)

            connect(body.id, ConstraintSet.TOP, header.id, ConstraintSet.BOTTOM, 8)
            connect(body.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
            if (yandexView == null) {
                connect(body.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
            } else {
                connect(yandexView.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
                connect(body.id, ConstraintSet.BOTTOM, yandexView.id, ConstraintSet.TOP, 8)
                connect(yandexView.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
            }

            connect(timer.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 4)
            connect(timer.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 4)

            applyTo(this@AccidentRow)
        }

        setOnLongClickListener {
            AccidentContextMenu(context, accident).showAsDropDown(it)
            true
        }
    }
}