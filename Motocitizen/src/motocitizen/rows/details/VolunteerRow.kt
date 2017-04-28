package motocitizen.rows.details

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.TableRow
import android.widget.TextView

import motocitizen.content.Volunteer
import motocitizen.main.R
import motocitizen.utils.DateUtils

@SuppressLint("ViewConstructor")
open class VolunteerRow(context: Context, volunteer: Volunteer) : TableRow(context) {
    init {
        LayoutInflater.from(context).inflate(R.layout.volunteer_row, this, true)
        (this.findViewById(R.id.volunteer) as TextView).text = volunteer.name
        (this.findViewById(R.id.action) as TextView).text = volunteer.status.string()
        (this.findViewById(R.id.time) as TextView).text = DateUtils.getTime(volunteer.time)
    }
}
