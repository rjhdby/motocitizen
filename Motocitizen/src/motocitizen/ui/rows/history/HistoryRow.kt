package motocitizen.ui.rows.history

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import motocitizen.content.history.History
import motocitizen.main.R
import motocitizen.user.User
import motocitizen.utils.dateTimeString
import motocitizen.utils.getStringTime

class HistoryRow : LinearLayout {
    //todo refactor
    constructor(context: Context, history: History) : super(context) {
        layoutParams = generateDefaultLayoutParams()
        orientation = LinearLayout.HORIZONTAL

        LayoutInflater.from(context).inflate(R.layout.history_row, this, true)

        val ownerView = this.findViewById(R.id.owner) as TextView
        if (history.owner == User.id) {
            ownerView.setBackgroundColor(Color.DKGRAY)
        }
        ownerView.text = history.ownerName()
        (this.findViewById(R.id.text) as TextView).text = history.action.text
        (this.findViewById(R.id.date) as TextView).text = history.time.dateTimeString()
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
}
