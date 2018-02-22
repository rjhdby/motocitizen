package motocitizen.ui.activity

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.method.LinkMovementMethod
import motocitizen.main.R
import motocitizen.ui.Screens
import motocitizen.ui.changelog.ChangeLog
import motocitizen.utils.goTo
import motocitizen.utils.lparamsMatchParent
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick

class AboutActivity : AppCompatActivity() {
    val layout by lazy {
        verticalLayout {
            lparamsMatchParent()

            textView(R.string.about_info)
            textView(getString(R.string.code_version_prefix) + ": " + packageManager.getPackageInfo(packageName, 0).versionName)
            textView(R.string.about_url_support) {
                movementMethod = LinkMovementMethod.getInstance()
            }
            textView(R.string.about_authors)
            button(R.string.business_card) {
                onClick {
                    goTo(Screens.BUSINESS_CARD)
                }
            }.lparams { width = matchParent }
            webView {
                lparamsMatchParent()
                backgroundColor = Color.rgb(48, 48, 48)
            }.loadDataWithBaseURL(null, ChangeLog.getLog(this@AboutActivity), "text/html", "UTF-8", null)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
