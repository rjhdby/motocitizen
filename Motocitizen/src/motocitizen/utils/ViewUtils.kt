package motocitizen.utils

import android.view.View
import android.view.ViewGroup

fun <T : View> T.lparamsMatchParent(): T {
    val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    this@lparamsMatchParent.layoutParams = layoutParams
    return this
}

fun View.hide() = changeVisibility(View.INVISIBLE)
fun View.show() = changeVisibility(View.VISIBLE)
fun View.gone() = changeVisibility(View.GONE)

private fun View.changeVisibility(newVisibility: Int) {
    visibility = newVisibility
}