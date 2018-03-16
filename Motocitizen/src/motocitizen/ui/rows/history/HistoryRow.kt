package motocitizen.ui.rows.history

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.LinearLayout
import motocitizen.content.history.History
import motocitizen.user.User
import motocitizen.utils.dateTimeString
import motocitizen.utils.name
import org.jetbrains.anko.textView

class HistoryRow : LinearLayout {
    private lateinit var history: History

    constructor(context: Context, history: History) : super(context) {
        this.history = history
        layoutParams = generateDefaultLayoutParams()
        orientation = LinearLayout.HORIZONTAL
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        textView(history.owner.name()) { if (history.owner == User.id) setBackgroundColor(Color.DKGRAY) }
        textView(history.action.text)
        textView(history.time.dateTimeString())
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
}
