package motocitizen.ui.activity

import android.graphics.Color
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import motocitizen.main.R
import motocitizen.ui.Screens
import motocitizen.ui.changelog.ChangeLog
import motocitizen.utils.goTo

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // Корневой ScrollView (чтобы содержимое скроллилось)
        val scrollView = ScrollView(this).apply {
            setBackgroundColor(Color.rgb(48, 48, 48))
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        // Вертикальный LinearLayout внутри ScrollView
        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val padding = (16 * resources.displayMetrics.density).toInt()
            setPadding(padding, padding, padding, padding)
        }

        scrollView.addView(rootLayout)

        // TextView: about_info
        val aboutInfoText = TextView(this).apply {
            setText(R.string.about_info)
            setTextColor(Color.WHITE)
        }
        rootLayout.addView(aboutInfoText)

        // TextView: версия приложения
        val versionName = packageManager.getPackageInfo(packageName, 0).versionName
        val versionText = TextView(this).apply {
            text = getString(R.string.code_version_prefix) + ": $versionName"
            setTextColor(Color.WHITE)
            setPadding(0, (8 * resources.displayMetrics.density).toInt(), 0, 0)
        }
        rootLayout.addView(versionText)

        // TextView: about_url_support (с активными ссылками)
        val supportUrl = TextView(this).apply {
            setText(R.string.about_url_support)
            setTextColor(Color.CYAN)
            movementMethod = LinkMovementMethod.getInstance()
            setPadding(0, (8 * resources.displayMetrics.density).toInt(), 0, 0)
            // Чтобы ссылки были кликабельны, нужно еще autoLink:
            autoLinkMask = Linkify.WEB_URLS
        }
        rootLayout.addView(supportUrl)

        // TextView: about_authors
        val authorsText = TextView(this).apply {
            setText(R.string.about_authors)
            setTextColor(Color.WHITE)
            setPadding(0, (8 * resources.displayMetrics.density).toInt(), 0, 0)
        }
        rootLayout.addView(authorsText)

        // Кнопка business_card
        val businessCardBtn = Button(this).apply {
            setText(R.string.business_card)
            setPadding(0, (16 * resources.displayMetrics.density).toInt(), 0, 0)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setOnClickListener {
                goTo(Screens.BUSINESS_CARD)
            }
        }
        rootLayout.addView(businessCardBtn)

        // WebView для ChangeLog
        val webView = WebView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = (16 * resources.displayMetrics.density).toInt()
            }
            setBackgroundColor(Color.rgb(48, 48, 48))
            loadDataWithBaseURL(
                null,
                ChangeLog.getLog(this@AboutActivity),
                "text/html",
                "UTF-8",
                null
            )
        }
        rootLayout.addView(webView)

        setContentView(scrollView)
    }
}