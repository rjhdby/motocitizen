package motocitizen.ui.rows.accident

import android.content.Context
import motocitizen.content.accident.Accident
import motocitizen.main.R

class OwnedActiveRow(context: Context, accident: Accident) : OwnedRow(context, accident) {
    override val textColor: Int = ACTIVE_COLOR
    override val background: Int = R.drawable.owner_message_row
}
