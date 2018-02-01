package motocitizen.ui.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import motocitizen.datasources.preferences.Preferences
import motocitizen.main.R
import motocitizen.permissions.Permissions
import motocitizen.router.Router
import motocitizen.user.Auth
import motocitizen.user.User

class StartupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!isTaskRoot) {
            finish()
            return
        }
        setContentView(R.layout.activity_startup)
    }

    //TODO проверка разрешений
    public override fun onResume() {
        super.onResume()
        Permissions.requestLocation(this, this::ahead)
    }

    //todo enum
    private fun ahead() {
        Auth.autoAuth { Router.goTo(this@StartupActivity, if (User.isAuthorized) Router.Target.MAIN else Router.Target.AUTH) }
//        when (Preferences.authType) {
//            "none"  -> Router.goTo(this, Router.Target.AUTH)
//            "anon"  -> Router.goTo(this, Router.Target.MAIN)
//            "forum" -> tryToLogon()
//            "vk"    -> vkLogin()
//            else    -> tryToLogon()
//        }
    }

//    private fun vkLogin() {
//        Auth.auth(Auth.AuthType.VK) {
//            Router.goTo(this@StartupActivity, if (User.isAuthorized) Router.Target.MAIN else Router.Target.AUTH)
//        }
//    }

//    private fun tryToLogon() {
//        Auth.auth(Auth.AuthType.FORUM) {
//            Router.goTo(this@StartupActivity, if (User.isAuthorized) Router.Target.MAIN else Router.Target.AUTH)
//        }
//    }
}
