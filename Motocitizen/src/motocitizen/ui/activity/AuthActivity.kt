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
    private val loginField: EditText by bindView(R.id.auth_login)
    private val passwordField: EditText by bindView(R.id.auth_password)
    private val anonymous: Button by bindView(R.id.anonymous)
    private val forum: Button by bindView(R.id.forum)
    private val forumLoginForm: View by bindView(R.id.forum_login_form)
    private val authErrorHelper: TextView by bindView(R.id.auth_error_helper)

    private fun enableLoginBtn() {
        loginBtn.isEnabled = loginField.text.toString().isNotEmpty() && passwordField.text.toString().isNotEmpty()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, vkCallback())) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun vkCallback(): VKCallback<VKAccessToken> {
        return object : VKCallback<VKAccessToken> {
            override fun onResult(res: VKAccessToken) {
                Auth.auth(Auth.AuthType.VK) { toMainScreen() }
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

        initializeScreen()
    }

    private fun setUpListeners() {
        loginVK.setOnClickListener { VKSdk.login(this@AuthActivity, VKScope.PAGES) }
        loginBtn.setOnClickListener { loginButtonPressed() }
        loginField.afterTextChanged { enableLoginBtn() }
        passwordField.afterTextChanged { enableLoginBtn() }
        anonymous.setOnClickListener { anonymousLogon() }
        forum.setOnClickListener { forumLoginForm.visibility = View.VISIBLE }
    }

    private fun loginButtonPressed() = when {
        isOnline -> auth()
        else     -> showToast(R.string.auth_not_available)
    }

    private fun anonymousLogon() {
        Auth.auth(Auth.AuthType.ANON) {}
        toMainScreen()
    }

    private val isOnline: Boolean
        get() = MyApp.isOnline(this)

    private fun vkWakeUpSession() {
        VKSdk.wakeUpSession(this, object : VKCallback<VKSdk.LoginState> {
            override fun onResult(res: VKSdk.LoginState) = when (res) {
                VKSdk.LoginState.LoggedIn -> toMainScreen()
                else                      -> Unit
            }

            override fun onError(error: VKError) {}
        })
    }

    private fun initializeScreen() {
        loginField.setText(Preferences.login)
        passwordField.setText(Preferences.password)
        arrayOf(loginBtn, loginField, passwordField).forEach { it.isEnabled = !User.isAuthorized }
    }

    private fun auth() {
        Preferences.apply {
            login = loginField.text.toString()
            password = passwordField.text.toString()
        }
        Auth.auth(Auth.AuthType.FORUM) { authCallback() }
    }

    private fun authCallback() = when {
        User.isAuthorized -> toMainScreen()
        else              -> authErrorHelper.setText(R.string.auth_password_error)
    }

    private fun toMainScreen() = goTo(Screens.MAIN)
}
