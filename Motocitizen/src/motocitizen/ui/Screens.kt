package motocitizen.ui

import motocitizen.ui.activity.*

enum class Screens(val activity: Class<*>) {
    ABOUT(AboutActivity::class.java),
    DETAILS(AccidentDetailsActivity::class.java),
    AUTH(AuthActivity::class.java),
    BUSINESS_CARD(BusinessCardActivity::class.java),
    CREATE(CreateAccActivity::class.java),
    MAIN(MainScreenActivity::class.java),
    SETTINGS(SettingsActivity::class.java),
    STARTUP(StartupActivity::class.java)
}