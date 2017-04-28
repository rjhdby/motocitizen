package motocitizen.rows.accidentList

import android.content.Context
import motocitizen.content.accident.Accident
import motocitizen.main.R

class HiddenOwnedRow(context: Context, accident: Accident) : OwnedRow(context, accident) {
    override val textColor: Int
        get() = HIDDEN_COLOR
    override val background: Int
        get() = R.drawable.owner_accident_hidden
}
