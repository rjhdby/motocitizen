package motocitizen.ui.activity

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.method.LinkMovementMethod
import android.view.Menu
import motocitizen.datasources.preferences.Preferences
import motocitizen.main.R
import motocitizen.router.Router
import motocitizen.ui.changelog.ChangeLog
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = verticalLayout {
            lparams {
                width = matchParent
                height = matchParent
            }

            textView(R.string.about_info)
            textView(getString(R.string.code_version_prefix) + ": " + Preferences.appVersion)
            textView(R.string.about_url_support) {
                movementMethod = LinkMovementMethod.getInstance()
            }
            textView(R.string.about_authors)
            button(R.string.business_card) {
                onClick {
                    Router.goTo(this@AboutActivity, Router.Target.BUSINESS_CARD)
                }
            }.lparams { width = matchParent }
            webView {
                lparams {
                    width = matchParent
                    height = matchParent
                }

                backgroundColor = Color.rgb(48, 48, 48)
            }.loadDataWithBaseURL(null, ChangeLog.getLog(this@AboutActivity), "text/html", "UTF-8", null)

        }
        setContentView(layout)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean = true
}
