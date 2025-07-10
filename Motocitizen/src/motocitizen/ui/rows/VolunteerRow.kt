package motocitizen.ui.rows

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TableRow
import android.widget.TextView
import motocitizen.content.volunteer.VolunteerAction
import motocitizen.utils.bothMatch
import motocitizen.utils.name
import motocitizen.utils.timeString

@SuppressLint("ViewConstructor")
open class VolunteerRow(context: Context, val volunteer: VolunteerAction) : TableRow(context) {
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        layoutParams = bothMatch()

        addView(createTextView(volunteer.owner.name()))
        addView(createTextView(volunteer.status.text))
        addView(createTextView(volunteer.time.timeString()))
    }
    private fun createTextView(text: String): TextView {
        return TextView(context).apply {
            this.text = text
            layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                1f
            )
            setPadding(16, 8, 16, 8)
        }
    }
}