package motocitizen.rows.accidentList

import android.content.Context
import motocitizen.content.accident.Accident
import motocitizen.main.R

class OwnedHiddenRow(context: Context, accident: Accident) : OwnedRow(context, accident) {
    override val textColor: Int = HIDDEN_COLOR
    override val background: Int = R.drawable.owner_accident_hidden
}
