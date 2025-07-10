package motocitizen.utils

import android.view.ViewGroup

class Margins(var left: Int = 0, var top: Int = 0, var right: Int = 0, var bottom: Int = 0)

inline fun <reified T : ViewGroup.MarginLayoutParams> T.margins(m: Margins): T {
    setMargins(m.left, m.top, m.right, m.bottom)
    return this
}