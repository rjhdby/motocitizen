package motocitizen.ui

import motocitizen.ui.activity.AboutActivity
import motocitizen.ui.activity.AuthActivity
import motocitizen.ui.activity.BusinessCardActivity
import motocitizen.ui.activity.CreateAccActivity
import motocitizen.ui.activity.MainScreenActivity
import motocitizen.ui.activity.SettingsActivity
import motocitizen.ui.activity.StartupActivity

enum class Screens(val activity: Class<*>) {
    ABOUT(AboutActivity::class.java),
    AUTH(AuthActivity::class.java),
    BUSINESS_CARD(BusinessCardActivity::class.java),
    CREATE(CreateAccActivity::class.java),
    MAIN(MainScreenActivity::class.java),
    SETTINGS(SettingsActivity::class.java),
    STARTUP(StartupActivity::class.java)
}