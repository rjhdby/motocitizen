package motocitizen.rows.volunteer

import android.content.Context
import motocitizen.content.volunteer.VolunteerAction

object VolunteerRowFactory {
    fun make(context: Context, volunteer: VolunteerAction): VolunteerRow = VolunteerRow(context, volunteer)
}