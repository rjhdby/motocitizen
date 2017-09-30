package motocitizen.ui.rows.sound

import android.content.Context
import android.widget.LinearLayout
import android.widget.TableRow
import motocitizen.utils.dp
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textView

class SoundRow(context: Context, val title: String) : TableRow(context) {
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        layoutParams = LayoutParams(matchParent, matchParent)
        textView(title)
        linearLayout {
            orientation = LinearLayout.VERTICAL
            layoutParams = LayoutParams(matchParent, 2.dp())
        }
    }
}
