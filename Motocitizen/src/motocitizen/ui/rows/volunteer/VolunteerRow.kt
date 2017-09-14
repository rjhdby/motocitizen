package motocitizen.ui.rows.volunteer

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.TableRow
import android.widget.TextView
import motocitizen.content.Content
import motocitizen.content.volunteer.VolunteerAction
import motocitizen.main.R
import motocitizen.utils.getTime

@SuppressLint("ViewConstructor")
open class VolunteerRow(context: Context, volunteer: VolunteerAction) : TableRow(context) {
    init {
        LayoutInflater.from(context).inflate(R.layout.volunteer_row, this, true)
        (this.findViewById(R.id.volunteer) as TextView).text = Content.volunteers[volunteer.id]?.name ?: "unknown"
        (this.findViewById(R.id.action) as TextView).text = volunteer.status.text //todo
        (this.findViewById(R.id.time) as TextView).text = getTime(volunteer.time)
    }
}
