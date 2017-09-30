package motocitizen.ui.rows.volunteer

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TableRow
import motocitizen.content.volunteer.VolunteerAction
import motocitizen.utils.timeString
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textView

@SuppressLint("ViewConstructor")
open class VolunteerRow(context: Context, val volunteer: VolunteerAction) : TableRow(context) {
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        layoutParams = LayoutParams(matchParent, matchParent)
        textView(volunteer.ownerName())
        textView(volunteer.status.text)
        textView(volunteer.time.timeString())
    }
}
