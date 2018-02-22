package motocitizen.ui.activity

import afterTextChanged
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKCallback
import com.vk.sdk.VKScope
import com.vk.sdk.VKSdk
import com.vk.sdk.api.VKError
import motocitizen.MyApp
import motocitizen.datasources.preferences.Preferences
import motocitizen.main.R
import motocitizen.ui.Screens
import motocitizen.user.Auth
import motocitizen.user.User
import motocitizen.utils.bindView
import motocitizen.utils.goTo
import motocitizen.utils.showToast

//todo refactor
class AuthActivity : AppCompatActivity() {
    private val loginBtn: Button by bindView(R.id.login_button)
    private val loginVK: Button by bindView(R.id.vk)
    private val login: EditText by bindView(R.id.auth_login)
    private val password: EditText by bindView(R.id.auth_password)
    private val anonymous: Button by bindView(R.id.anonymous)
    private val forum: Button by bindView(R.id.forum)
    private val forumLoginForm: View by bindView(R.id.forum_login_form)
//    private lateinit var loginBtn: Button
//    private lateinit var loginVK: Button
//    private lateinit var login: EditText
//    private lateinit var password: EditText
//    private lateinit var anonymous: Button
//    private lateinit var forum: Button
//    private lateinit var forumLoginForm: View
//
//    private fun bindViews() {
//        loginBtn = findViewById(R.id.login_button)
//        loginVK = findViewById(R.id.vk)
//        login = findViewById(R.id.auth_login)
//        password = findViewById(R.id.auth_password)
//        anonymous = findViewById(R.id.anonymous)
//        forum = findViewById(R.id.forum)
//        forumLoginForm = findViewById(R.id.forum_login_form)
//    }

    private fun enableLoginBtn() {
        loginBtn.isEnabled = login.text.toString().isNotEmpty() && password.text.toString().isNotEmpty()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, vkCallback())) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun vkCallback(): VKCallback<VKAccessToken> {
        return object : VKCallback<VKAccessToken> {
            override fun onResult(res: VKAccessToken) {
                Auth.auth(Auth.AuthType.VK) { goTo(Screens.MAIN) }
            }

            override fun onError(error: VKError) {
                showToast("Произошла ошибка авторизации (например, пользователь запретил авторизацию)")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.auth)
//        bindViews()
        setUpListeners()

        vkWakeUpSession()

        fillCtrls()
    }

    private fun setUpListeners() {
        loginVK.setOnClickListener { VKSdk.login(this@AuthActivity, VKScope.PAGES) }
        loginBtn.setOnClickListener { loginButtonPressed() }
        login.afterTextChanged { enableLoginBtn() }
        password.afterTextChanged { enableLoginBtn() }
        anonymous.setOnClickListener { anonymousLogon() }
        forum.setOnClickListener { forumLoginForm.visibility = View.VISIBLE }
    }

    private fun loginButtonPressed() {
        when {
            isOnline -> auth()
            else     -> showToast(R.string.auth_not_available)
        }
    }

    private fun anonymousLogon() {
        (findViewById<TextView>(R.id.auth_error_helper)).text = ""
        Auth.auth(Auth.AuthType.ANON) {}
        goTo(Screens.MAIN)
    }

    private val isOnline: Boolean
        get() = MyApp.isOnline(this)

    private fun vkWakeUpSession() {
        VKSdk.wakeUpSession(this, object : VKCallback<VKSdk.LoginState> {
            override fun onResult(res: VKSdk.LoginState) {
                when (res) {
                    VKSdk.LoginState.LoggedOut -> Unit
                    VKSdk.LoginState.LoggedIn  -> goTo(Screens.MAIN)
                    VKSdk.LoginState.Pending   -> Unit
                    VKSdk.LoginState.Unknown   -> Unit
                }
            }

            override fun onError(error: VKError) {

            }
        })
    }

    //todo refactor
    private fun fillCtrls() {
        login.setText(Preferences.login)
        password.setText(Preferences.password)

        val isAuthorized = User.isAuthorized
        loginBtn.isEnabled = !isAuthorized
        login.isEnabled = !isAuthorized
        password.isEnabled = !isAuthorized
    }

    private fun auth() {
        Preferences.login = login.text.toString()
        Preferences.password = password.text.toString()
        Auth.auth(Auth.AuthType.FORUM) { authCallback() }
    }

    private fun authCallback() {
        when {
            User.isAuthorized -> goTo(Screens.MAIN)
            else              -> showAuthError()
        }
    }

    private fun showAuthError() {
        this@AuthActivity.runOnUiThread {
            val authErrorHelper = findViewById<TextView>(R.id.auth_error_helper)
            authErrorHelper.setText(R.string.auth_password_error)
        }
    }
}
