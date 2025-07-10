package motocitizen.ui.rows

import android.content.Context
import android.graphics.Color
import android.widget.LinearLayout
import android.widget.TextView
import motocitizen.content.history.History
import motocitizen.user.User
import motocitizen.utils.bothWrap
import motocitizen.utils.dateTimeString
import motocitizen.utils.name

class HistoryRow(context: Context, private val history: History) : LinearLayout(context) {
    init {
        layoutParams = generateDefaultLayoutParams()
        orientation = HORIZONTAL
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        addView(createTextView(history.owner.name()).apply {
            if (history.owner == User.id) {
                setBackgroundColor(Color.DKGRAY)
            }
        })

        addView(createTextView(history.action.text).apply {
            setPadding(5, 0, 0, 0)
        })

        addView(createTextView(history.time.dateTimeString()).apply {
            setPadding(5, 0, 0, 0)
        })
    }

    private fun createTextView(text: String): TextView = TextView(context).apply {
        this.text = text
        layoutParams = bothWrap()
        setTextColor(Color.BLACK)
    }
}