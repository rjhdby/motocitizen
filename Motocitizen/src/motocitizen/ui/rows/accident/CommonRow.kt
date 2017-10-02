package motocitizen.ui.rows.accident

import android.content.Context
import motocitizen.content.accident.Accident
import motocitizen.utils.MarginArray

abstract class CommonRow(context: Context, accident: Accident) : Row(context, accident) {
    override val margins = MarginArray(4, 2, 16, 2)
}
