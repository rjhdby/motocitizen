package motocitizen.rows

import android.content.Context
import motocitizen.content.accident.Accident
import motocitizen.content.history.History
import motocitizen.content.message.Message
import motocitizen.content.volunteer.Volunteer
import motocitizen.content.volunteer.VolunteerAction
import motocitizen.dictionary.AccidentStatus
import motocitizen.rows.accidentList.*
import motocitizen.rows.details.HistoryRow
import motocitizen.rows.details.VolunteerRow
import motocitizen.rows.message.MessageRow
import motocitizen.rows.message.OwnMessageRow
import motocitizen.user.User

object RowFactory {
    fun make(context: Context, accident: Accident): Row {
        val userId = User.dirtyRead().id
        val row = when {
            accident.status == AccidentStatus.ACTIVE && accident.owner == userId -> OwnedActiveRow(context, accident)
            accident.status == AccidentStatus.ACTIVE                             -> ActiveRow(context, accident)
            accident.status != AccidentStatus.ACTIVE && accident.owner == userId -> OwnedEndedRow(context, accident)
            accident.status != AccidentStatus.ACTIVE                             -> OwnedEndedRow(context, accident)
            accident.status == AccidentStatus.HIDDEN && accident.owner == userId -> OwnedHiddenRow(context, accident)
            accident.status == AccidentStatus.HIDDEN                             -> OwnedHiddenRow(context, accident)
            else                                                                 -> OwnedActiveRow(context, accident)
        }
        row.bind()
        return row
    }

    fun make(context: Context, message: Message, last: Int, next: Int): MessageRow = if (message.isOwner) OwnMessageRow(context, message, last, next) else MessageRow(context, message, last, next)
    fun make(context: Context, history: History): HistoryRow = HistoryRow(context, history)
    fun make(context: Context, volunteer: VolunteerAction): VolunteerRow = VolunteerRow(context, volunteer)
}