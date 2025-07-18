package motocitizen.utils

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.model.LatLng
import motocitizen.ui.Screens

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

fun Activity.goTo(target: Screens) = startActivity(Intent(this, target.activity))

fun Activity.toExternalMap(latLng: LatLng) {
    val uri = "geo:${latLng.latitude},${latLng.longitude}"
    val intent = Intent(Intent.ACTION_VIEW, uri.toUri())
    startActivity(intent)
}

fun <T : View> Fragment.bindView(id: Int) = lazy { requireActivity().findViewById<T>(id) }

fun <T : View> Activity.bindView(id: Int, topPadding: Int = 0) = lazy {
    val view = findViewById<T>(id)
    view.setPadding(0, topPadding, 0, 0)
    return@lazy view
}

fun AppCompatActivity.padToolbars(view: View) {
    ViewCompat.setOnApplyWindowInsetsListener(view) { view, windowInsets ->
        val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
        view.updatePadding(top = insets.top)
        WindowInsetsCompat.CONSUMED
    }
}