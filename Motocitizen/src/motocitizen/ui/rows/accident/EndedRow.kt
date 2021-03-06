package motocitizen.ui.rows.accident

import android.content.Context
import motocitizen.content.accident.Accident
import motocitizen.main.R

class EndedRow(context: Context, accident: Accident) : CommonRow(context, accident) {
    override val textColor: Int = ENDED_COLOR
    override val background: Int = R.drawable.accident_row_ended
}
