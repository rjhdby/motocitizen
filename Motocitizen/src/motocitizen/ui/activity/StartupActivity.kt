package motocitizen.ui.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import motocitizen.main.R
import motocitizen.permissions.Permissions
import motocitizen.ui.Screens
import motocitizen.user.Auth
import motocitizen.user.User
import motocitizen.utils.goTo

class StartupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!isTaskRoot) {
            finish()
            return
        }
        setContentView(R.layout.activity_startup)
    }

    public override fun onResume() {
        super.onResume()
        Permissions.requestLocation(this) { ahead() }
    }

    private fun ahead() = Auth.autoAuth { goTo(if (User.isAuthorized) Screens.MAIN else Screens.AUTH) }
}
