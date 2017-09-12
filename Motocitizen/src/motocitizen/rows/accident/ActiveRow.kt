package motocitizen.rows.accident

import android.content.Context
import motocitizen.content.accident.Accident
import motocitizen.main.R

class ActiveRow(context: Context, accident: Accident) : CommonRow(context, accident) {
    override val textColor: Int = ACTIVE_COLOR
    override val background: Int = R.drawable.message_row
}
