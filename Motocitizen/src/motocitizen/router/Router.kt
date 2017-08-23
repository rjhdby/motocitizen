package motocitizen.router

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.google.android.gms.maps.model.LatLng
import motocitizen.activity.*

object Router {
    enum class Target constructor(val activity: Class<*>) {
        ABOUT(AboutActivity::class.java),
        DETAILS(AccidentDetailsActivity::class.java),
        AUTH(AuthActivity::class.java),
        BUSINESS_CARD(BusinessCardActivity::class.java),
        CREATE(CreateAccActivity::class.java),
        MAIN(MainScreenActivity::class.java),
        SETTINGS(SettingsActivity::class.java),
        STARTUP(StartupActivity::class.java)
    }

    @JvmOverloads
    fun goTo(activity: Activity, target: Target, bundle: Bundle = Bundle()) {
        val intent = Intent(activity, target.activity)
        intent.putExtras(bundle)
        activity.startActivity(intent)
    }

    fun dial(activity: Activity, phone: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:+" + phone)
        activity.startActivity(intent)
    }

    fun sms(activity: Activity, phone: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("sms:" + phone)
        activity.startActivity(intent)
    }

    fun share(activity: Activity, text: String) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, text)
        sendIntent.type = "text/plain"
        activity.startActivity(sendIntent)
    }

    //TODO EXTERMINATUS!!!!
    fun exit(activity: Activity) {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        activity.startActivity(intent)
        val pid = android.os.Process.myPid()
        android.os.Process.killProcess(pid)
    }

    fun toExternalMap(activity: Activity, latLng: LatLng) {
        val uri = "geo:" + latLng.latitude + "," + latLng.longitude
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        activity.startActivity(intent)
    }
}
