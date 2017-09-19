package motocitizen.ui.activity

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.method.LinkMovementMethod
import android.view.Menu
import android.webkit.WebView
import android.widget.TextView
import motocitizen.main.R
import motocitizen.router.Router
import motocitizen.ui.changelog.ChangeLog
import motocitizen.datasources.preferences.Preferences

//Посмотреть http://android-developers.blogspot.in/2013/08/actionbarcompat-and-io-2013-app-source.html
class AboutActivity : AppCompatActivity() {
    private val ROOT_LAYOUT = R.layout.activity_about
    private val VERSION = R.id.about_code_version
    private val BUSINESS_CARD_BUTTON = R.id.businessCardButton
    private val URL_VIEW = R.id.about_url_support
    private val WEB_VIEW = R.id.change_log
    private val changeLogColor = Color.rgb(48, 48, 48)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ROOT_LAYOUT)

        (this.findViewById(VERSION) as TextView).text = getString(R.string.code_version_prefix) + ": " + Preferences.appVersion

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        loadChangeLog()

        findViewById(BUSINESS_CARD_BUTTON).setOnClickListener { _ -> Router.goTo(this, Router.Target.BUSINESS_CARD) }

        (findViewById(URL_VIEW) as TextView).movementMethod = LinkMovementMethod.getInstance()
    }

    private fun loadChangeLog() {
        val webView = findViewById(WEB_VIEW) as WebView
        webView.setBackgroundColor(changeLogColor)
        webView.loadDataWithBaseURL(null, ChangeLog.getLog(this), "text/html", "UTF-8", null)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean = true
}
