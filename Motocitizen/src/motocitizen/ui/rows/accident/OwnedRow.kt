package motocitizen.ui.rows.accident

import android.content.Context
import motocitizen.content.accident.Accident
import motocitizen.utils.makeMargins

abstract class OwnedRow(context: Context, accident: Accident) : Row(context, accident) {
    override val margins = makeMargins(2) {
        left = 16
        right = 4
    }
}