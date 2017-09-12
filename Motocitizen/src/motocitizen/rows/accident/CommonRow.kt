package motocitizen.rows.accident

import android.content.Context
import motocitizen.content.accident.Accident
import motocitizen.main.R

abstract class CommonRow(context: Context, accident: Accident) : Row(context, accident) {
    override val margins = arrayOf(4, 2, 16, 2)
    override val layout: Int = R.layout.accident_row
}
