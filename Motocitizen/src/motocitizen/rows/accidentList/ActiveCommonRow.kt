package motocitizen.rows.accidentList

import android.content.Context
import motocitizen.content.accident.Accident
import motocitizen.main.R

class ActiveCommonRow(context: Context, accident: Accident) : CommonRow(context, accident) {
    override val textColor: Int
        get() = ACTIVE_COLOR
    override val background: Int
        get() = R.drawable.message_row
}
