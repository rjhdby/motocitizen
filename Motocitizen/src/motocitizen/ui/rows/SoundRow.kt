package motocitizen.ui.rows

import android.content.Context
import android.widget.LinearLayout
import android.widget.TableRow
import android.widget.TextView
import motocitizen.utils.bothMatch
import motocitizen.utils.bothWrap
import motocitizen.utils.dp

class SoundRow(context: Context, val title: String) : TableRow(context) {
    init {
        layoutParams = bothMatch()

        val titleView = TextView(context).apply {
            text = title
            layoutParams = bothWrap()
        }
        addView(titleView)

        val lineView = LinearLayout(context).apply {
            orientation = VERTICAL
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                2.dp()
            )
        }
        addView(lineView)
    }
}