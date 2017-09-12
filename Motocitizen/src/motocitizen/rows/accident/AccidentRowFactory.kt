package motocitizen.rows.accident

import android.content.Context
import motocitizen.content.accident.Accident
import motocitizen.dictionary.AccidentStatus

object AccidentRowFactory {
    fun make(context: Context, accident: Accident): Row =
            if (accident.isOwner) makeOwnedRow(context, accident) else makeCommonRow(context, accident)

    private fun makeOwnedRow(context: Context, accident: Accident): Row = when (accident.status) {
        AccidentStatus.ACTIVE -> OwnedActiveRow(context, accident)
        AccidentStatus.HIDDEN -> OwnedHiddenRow(context, accident)
        else                  -> OwnedEndedRow(context, accident)
    }

    private fun makeCommonRow(context: Context, accident: Accident): Row = when (accident.status) {
        AccidentStatus.ACTIVE -> ActiveRow(context, accident)
        AccidentStatus.HIDDEN -> HiddenRow(context, accident)
        else                  -> EndedRow(context, accident)
    }
}