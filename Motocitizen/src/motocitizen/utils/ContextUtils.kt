package motocitizen.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

fun Context.copyToClipBoard(text: String) {
    (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).primaryClip = ClipData.newPlainText("text", text)
}