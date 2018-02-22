package motocitizen.utils

import android.view.View
import android.view.ViewGroup

fun <T : View> T.lparamsMatchParent(): T {
    val layoutParams = ViewGroup.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT)
    this@lparamsMatchParent.layoutParams = layoutParams
    return this
}

fun View.hide() {
    visibility = View.INVISIBLE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}