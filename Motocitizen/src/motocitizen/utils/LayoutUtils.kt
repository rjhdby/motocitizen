package motocitizen.utils

import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT

inline fun <reified T : ViewGroup.MarginLayoutParams> layoutParams(width: Int, height: Int): T {
    val ctor = T::class.java.getConstructor(Int::class.java, Int::class.java)
    return ctor.newInstance(width, height)
}

inline fun <reified T : ViewGroup.MarginLayoutParams> bothMatch(): T = layoutParams(MATCH_PARENT, MATCH_PARENT)
inline fun <reified T : ViewGroup.MarginLayoutParams> bothWrap(): T = layoutParams(WRAP_CONTENT, WRAP_CONTENT)
inline fun <reified T : ViewGroup.MarginLayoutParams> matchAndWrap(): T = layoutParams(MATCH_PARENT, WRAP_CONTENT)
inline fun <reified T : ViewGroup.MarginLayoutParams> wrapAndMatch(): T = layoutParams(WRAP_CONTENT, MATCH_PARENT)