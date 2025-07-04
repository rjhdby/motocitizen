package motocitizen.ui.rows.sound

import android.content.Context
import android.widget.LinearLayout
import android.widget.TableRow
import android.widget.TextView
import motocitizen.utils.dp

class SoundRow(context: Context, val title: String) : TableRow(context) {
    init {
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )

        val titleView = TextView(context).apply {
            text = title
            layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )
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
