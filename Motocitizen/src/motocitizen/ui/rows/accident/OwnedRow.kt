package motocitizen.ui.rows.accident

import android.content.Context
import motocitizen.content.accident.Accident
import motocitizen.utils.MarginArray

abstract class OwnedRow(context: Context, accident: Accident) : Row(context, accident) {
    override val margins = MarginArray(16, 2, 4, 2)
}