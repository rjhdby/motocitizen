package motocitizen.rows.accidentList

import android.content.Context
import motocitizen.content.accident.Accident
import motocitizen.main.R

class HiddenRow(context: Context, accident: Accident) : CommonRow(context, accident) {
    override val textColor: Int = HIDDEN_COLOR
    override val background: Int = R.drawable.accident_row_hidden
}
