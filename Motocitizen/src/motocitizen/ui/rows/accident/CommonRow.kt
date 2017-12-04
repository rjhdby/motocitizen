package motocitizen.ui.rows.accident

import android.content.Context
import motocitizen.content.accident.Accident
import motocitizen.utils.makeMargins

abstract class CommonRow(context: Context, accident: Accident) : Row(context, accident) {
    override val margins = makeMargins(2) {
        left = 4
        right = 16
    }
}
