package motocitizen.utils

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng
import motocitizen.ui.Screens
import androidx.core.net.toUri
import androidx.fragment.app.Fragment

fun Context.copyToClipBoard(text: String) {
    (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(ClipData.newPlainText("text", text))
}

fun Activity.showToast(text: String) = runOnUiThread { Toast.makeText(this, text, Toast.LENGTH_LONG).show() }

fun Activity.showToast(resource: Int) = runOnUiThread { showToast(getString(resource)) }

fun Context.makeDial(number: String) = try {
    val intent = Intent(Intent.ACTION_DIAL, "tel:$number".toUri())
    startActivity(intent)
} catch (e: Exception) {
    e.printStackTrace()
}

fun Activity.goTo(target: Screens, bundle: Map<String, Any>) {
    val intentBundle = Bundle()
    bundle.forEach {
        when (it.value) {
            is Int -> intentBundle.putInt(it.key, it.value as Int)
            is String -> intentBundle.putString(it.key, it.value as String)
        }
    }
    val intent = Intent(this, target.activity)
    intent.putExtras(intentBundle)
    startActivity(intent)
}

fun Activity.goTo(target: Screens) = startActivity(Intent(this, target.activity))

fun Activity.toExternalMap(latLng: LatLng) {
    val uri = "geo:${latLng.latitude},${latLng.longitude}"
    val intent = Intent(Intent.ACTION_VIEW, uri.toUri())
    startActivity(intent)
}

fun AppCompatActivity.changeFragmentTo(containerView: Int, fragment: Fragment) = supportFragmentManager
    .beginTransaction()
    .replace(containerView, fragment)
    .commit()

fun <T : View> Fragment.bindView(id: Int) = lazy { requireActivity().findViewById<T>(id) }

fun <T : View> Activity.bindView(id: Int, topPadding: Int = 0) = lazy {
    val view = findViewById<T>(id)
    view.setPadding(0, topPadding, 0, 0)
    return@lazy view
}

private fun Context.obtainActionBarHeight(): Int {
    val styledAttrs = theme.obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))
    val height = styledAttrs.getDimensionPixelSize(0, 0)
    styledAttrs.recycle()
    return height
}

fun <T : View> Activity.bindViewWithActionBar(id: Int) = lazy {
    val view = findViewById<T>(id)
    val actionBarHeight = obtainActionBarHeight()
    view.setPadding(0, actionBarHeight, 0, 0)
    return@lazy view
}