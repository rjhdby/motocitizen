package motocitizen.rows.accidentList

import android.content.Context
import motocitizen.content.accident.Accident
import motocitizen.main.R

abstract class CommonRow(context: Context, accident: Accident) : Row(context, accident) {
    override fun changeMargins() {
        mLayoutParams.setMargins(4, 2, 16, 2)
    }

    override val layout: Int
        get() = R.layout.accident_row
}
