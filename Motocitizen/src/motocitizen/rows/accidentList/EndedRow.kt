package motocitizen.rows.accidentList

import android.content.Context
import motocitizen.content.accident.Accident
import motocitizen.main.R

class EndedRow(context: Context, accident: Accident) : CommonRow(context, accident) {
    override val textColor: Int
        get() = ENDED_COLOR
    override val background: Int
        get() = R.drawable.accident_row_ended
}
