package motocitizen.ui.activity

import afterTextChanged
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.*
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKCallback
import com.vk.sdk.VKScope
import com.vk.sdk.VKSdk
import com.vk.sdk.api.VKApi
import com.vk.sdk.api.VKError
import com.vk.sdk.api.VKRequest
import com.vk.sdk.api.VKResponse
import motocitizen.MyApp
import motocitizen.datasources.preferences.Preferences
import motocitizen.main.R
import motocitizen.router.Router
import motocitizen.user.Auth
import motocitizen.user.User
import motocitizen.utils.show

class AuthActivity : AppCompatActivity() {

    private lateinit var logoutBtn: Button
    private lateinit var loginBtn: Button
    private lateinit var cancelBtn: Button
    private lateinit var loginVK: Button
    private lateinit var login: EditText
    private lateinit var password: EditText
    private lateinit var anonymous: CheckBox

    private fun enableLoginBtn() {
        val logPasReady = login.text.toString().isNotEmpty() && password.text.toString().isNotEmpty()
        loginBtn.isEnabled = anonymous.isChecked || logPasReady
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, vkCallback())) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun vkCallback(): VKCallback<VKAccessToken> {
        return object : VKCallback<VKAccessToken> {
            override fun onResult(res: VKAccessToken) {
                Toast.makeText(applicationContext, "Пользователь успешно авторизовался", Toast.LENGTH_LONG).show()
            }

            override fun onError(error: VKError) {
                Toast.makeText(applicationContext, "Произошла ошибка авторизации (например, пользователь запретил авторизацию)", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.auth)
        bindViews()
        setUpListeners()
//todo ???
        if (User.isAuthorized) {
            Router.goTo(this, Router.Target.MAIN)
        }

        vkWakeUpSession()

        (findViewById(R.id.auth_error_text) as TextView).movementMethod = LinkMovementMethod.getInstance()
        fillCtrls()
    }

    private fun bindViews() {
        login = findViewById(R.id.auth_login) as EditText
        password = findViewById(R.id.auth_password) as EditText
        anonymous = findViewById(R.id.auth_anonim) as CheckBox
        cancelBtn = findViewById(R.id.cancel_button) as Button
        logoutBtn = findViewById(R.id.logout_button) as Button
        loginBtn = findViewById(R.id.login_button) as Button
        loginVK = findViewById(R.id.vk) as Button
    }

    private fun setUpListeners() {
        loginVK.setOnClickListener { VKSdk.login(this@AuthActivity, VKScope.PAGES) }
        findViewById(R.id.vk333).setOnClickListener { vkAuth() }
        loginBtn.setOnClickListener { loginButtonPressed() }
        logoutBtn.setOnClickListener { logOutButtonPressed() }
        login.afterTextChanged { enableLoginBtn() }
        password.afterTextChanged { enableLoginBtn() }
        anonymous.setOnClickListener { anonymousCheckBoxPressed() }
        cancelBtn.setOnClickListener { finish() }
    }

    private fun loginButtonPressed() {
        Preferences.anonymous = anonymous.isChecked
        when {
            anonymous.isChecked -> anonymousLogon()
            isOnline            -> auth()
            else                -> show(this, R.string.auth_not_available)
        }
    }

    private fun anonymousLogon() {
        (findViewById(R.id.auth_error_helper) as TextView).text = ""
        Router.goTo(this@AuthActivity, Router.Target.MAIN)
    }

    private val isOnline: Boolean
        get() = MyApp.isOnline(this)

    private fun logOutButtonPressed() {
        //TODO Добавить запрос подтверждения на выход.
        Preferences.resetAuth()
        Preferences.anonymous = true
        MyApp.logoff()
        fillCtrls()
    }

    private fun anonymousCheckBoxPressed() {
        login.isEnabled = !anonymous.isChecked
        password.isEnabled = !anonymous.isChecked
        enableLoginBtn()
    }

    private fun vkWakeUpSession() {
        VKSdk.wakeUpSession(this, object : VKCallback<VKSdk.LoginState> {
            override fun onResult(res: VKSdk.LoginState) {
                when (res) {
                    VKSdk.LoginState.LoggedOut -> Unit
                    VKSdk.LoginState.LoggedIn  -> Router.goTo(this@AuthActivity, Router.Target.MAIN)
                    VKSdk.LoginState.Pending   -> Unit
                    VKSdk.LoginState.Unknown   -> Unit
                }//showLogin();
                //showLogout();
            }

            override fun onError(error: VKError) {

            }
        })
    }

    private fun vkAuth() {
        VKApi.users().get().executeWithListener(object : VKRequest.VKRequestListener() {
            override fun onComplete(response: VKResponse) {
                super.onComplete(response)
            }

            override fun attemptFailed(request: VKRequest, attemptNumber: Int, totalAttempts: Int) {
                super.attemptFailed(request, attemptNumber, totalAttempts)
            }

            override fun onError(error: VKError) {
                super.onError(error)
            }

            override fun onProgress(progressType: VKRequest.VKProgressType, bytesLoaded: Long, bytesTotal: Long) {
                super.onProgress(progressType, bytesLoaded, bytesTotal)
            }
        })
    }

    //todo refactor
    private fun fillCtrls() {
        login.setText(Preferences.login)
        password.setText(Preferences.password)
        anonymous.isChecked = Preferences.anonymous
        //        View     accListYesterdayLine = findViewById(R.id.auth_error_text);
        val roleView = findViewById(R.id.role) as TextView

        val isAuthorized = User.isAuthorized
        loginBtn.isEnabled = !isAuthorized
        logoutBtn.isEnabled = isAuthorized
        anonymous.isEnabled = !isAuthorized
        //        accListYesterdayLine.setVisibility(isAuthorized ? View.GONE : View.VISIBLE);
        roleView.visibility = if (isAuthorized) View.VISIBLE else View.GONE
        login.isEnabled = !isAuthorized && !anonymous.isChecked
        password.isEnabled = !isAuthorized && !anonymous.isChecked
        //Авторизованы?
        if (isAuthorized) {
            roleView.text = String.format(getString(R.string.auth_role), User.roleName)
        } else {
            enableLoginBtn()
        }
    }

    private fun auth() {
        Auth.auth(login.text.toString(), password.text.toString()) { authCallback() }
    }

    private fun authCallback() {
        when {
            User.isAuthorized -> Router.goTo(this@AuthActivity, Router.Target.MAIN)
            else              -> showAuthError()
        }
    }

    private fun showAuthError() {
        this@AuthActivity.runOnUiThread {
            val authErrorHelper = findViewById(R.id.auth_error_helper) as TextView
            authErrorHelper.setText(R.string.auth_password_error)
        }
    }
}
