package motocitizen.router

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.google.android.gms.maps.model.LatLng
import motocitizen.ui.activity.*
import motocitizen.utils.makeDial
import org.jetbrains.anko.makeCall
import org.jetbrains.anko.sendSMS
import org.jetbrains.anko.share

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
        activity.makeDial(phone)
    }

    fun sms(activity: Activity, phone: String) {
        activity.sendSMS(phone)
    }

    fun share(activity: Activity, text: String) {
        activity.share(text)
    }

    fun toExternalMap(activity: Activity, latLng: LatLng) {
        val uri = "geo:${latLng.latitude},${latLng.longitude}"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        activity.startActivity(intent)
    }
}
