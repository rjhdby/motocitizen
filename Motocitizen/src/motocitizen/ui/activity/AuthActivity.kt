package motocitizen.ui.activity

import afterTextChanged
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKScope
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
    private val layout: LinearLayout by bindView(R.id.activity_auth)
    private val loginBtn: Button by bindView(R.id.login_button)
    private val loginVK: Button by bindView(R.id.vk)
    private val loginGoogle: Button by bindView(R.id.google)
    private val loginField: EditText by bindView(R.id.auth_login)
    private val passwordField: EditText by bindView(R.id.auth_password)
    private val anonymous: Button by bindView(R.id.anonymous)
    private val forum: Button by bindView(R.id.forum)
    private val forumLoginForm: View by bindView(R.id.forum_login_form)
    private val authErrorHelper: TextView by bindView(R.id.auth_error_helper)

    private val RC_SIGN_IN = 127

    private fun enableLoginBtn() {
        loginBtn.isEnabled = loginField.text.toString().isNotEmpty() && passwordField.text.toString().isNotEmpty()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RC_SIGN_IN -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                handleSignInResult(task)
            }
            else       -> if (data == null || !VK.onActivityResult(requestCode, resultCode, data, vkCallback())) {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            if (account == null) {
                showToast("Произошла ошибка авторизации (например, пользователь запретил авторизацию)")
            } else {
                Preferences.googleAccount = account.email!!
                Preferences.googleName = account.displayName!!
                Auth.auth(Auth.AuthType.GOOGLE) { toMainScreen() }
            }
        } catch (e: ApiException) {
            Log.w("AUTH ERROR", "signInResult:failed code=" + e.statusCode)
            showToast("Произошла ошибка авторизации (например, пользователь запретил авторизацию)")
        }
    }

    private fun vkCallback() = object : VKAuthCallback {
        override fun onLogin(token: VKAccessToken) {
            Preferences.vkToken = token.accessToken
            Auth.auth(Auth.AuthType.VK) { toMainScreen() }
        }

        override fun onLoginFailed(errorCode: Int) {
            showToast("Произошла ошибка авторизации (например, пользователь запретил авторизацию)")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.auth)
        setUpListeners()
        initializeScreen()
    }

    private fun setUpListeners() {
        loginVK.setOnClickListener { VK.login(this@AuthActivity, listOf(VKScope.PAGES, VKScope.OFFLINE)) }
        loginGoogle.setOnClickListener { googleAuth() }
        loginBtn.setOnClickListener { loginButtonPressed() }
        loginField.afterTextChanged { enableLoginBtn() }
        passwordField.afterTextChanged { enableLoginBtn() }
        anonymous.setOnClickListener { anonymousLogon() }
        forum.setOnClickListener { forumLoginForm.visibility = View.VISIBLE; layout.setVerticalGravity(Gravity.TOP) }
    }

    private fun googleAuth() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
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
        else              -> runOnUiThread { authErrorHelper.setText(R.string.auth_password_error) }
    }

    private fun toMainScreen() = goTo(Screens.MAIN)
}
