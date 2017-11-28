package motocitizen.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.DisplayMetrics
import android.widget.Toast
import org.jetbrains.anko.windowManager

fun Context.copyToClipBoard(text: String) {
    (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).primaryClip = ClipData.newPlainText("text", text)
}

fun Context.showToast(text: String) = Toast.makeText(this, text, Toast.LENGTH_LONG).show()

fun Context.showToast(resource: Int) = showToast(getString(resource))

fun Context.displayWidth(): Int {
    val displayMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics.widthPixels
}