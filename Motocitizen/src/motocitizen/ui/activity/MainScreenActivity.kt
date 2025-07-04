package motocitizen.ui.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import motocitizen.MyApp
import motocitizen.content.Content
import motocitizen.datasources.preferences.Preferences
import motocitizen.geo.geolocation.MyLocationManager
import motocitizen.geo.maps.MainMapManager
import motocitizen.main.R
import motocitizen.permissions.Permissions
import motocitizen.subscribe.SubscribeManager
import motocitizen.ui.Screens
import motocitizen.ui.changelog.ChangeLog
import motocitizen.ui.rows.accident.AccidentRowFactory
import motocitizen.ui.views.BounceScrollView
import motocitizen.user.User
import motocitizen.utils.*

class MainScreenActivity : AppCompatActivity() {
    companion object {
        private const val LIST: Byte = 0
        private const val MAP: Byte = 1
        private const val SUBSCRIBE_TAG = "mainScreen"
    }
    private val listenersScope = BackgroundScope.Default()

    private val mapContainer: ViewGroup by bindView(R.id.google_map)
    private val createAccButton: ImageButton by bindView(R.id.add_point_button)
    private val toAccListButton: ImageButton by bindView(R.id.list_button)
    private val toMapButton: ImageButton by bindView(R.id.map_button)
    private val accListView: View by bindView(R.id.acc_list)
    private val progressBar: ProgressBar by bindView(R.id.progressBar)
    private val listContent: ViewGroup by bindView(R.id.accListContent)
    private val dialButton: ImageButton by bindView(R.id.dial_button)

    private val bounceScrollView: BounceScrollView by bindView(R.id.accListRefresh)

    private var refreshItem: MenuItem? = null
    private lateinit var map: MainMapManager
    private var currentScreen = LIST

    private var transaction: Boolean = false
        set(value) {
            field = value
            refreshItem?.isVisible = !value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_screen_activity)
        map = MainMapManager(this)
        ChangeLog.show(this)
    }

    override fun onResume() {
        super.onResume()

        runBlocking {
            showCurrentFrame()
            setUpFeaturesAccessibility()
            val redraw = async { redraw() }
            setUpListeners()
            subscribe()
            launch {
                MyLocationManager.wakeup(this@MainScreenActivity)
            }
            redraw.await()
            requestAccidents()
        }
    }

    private fun showCurrentFrame() {
        setFrame(currentScreen)
    }

    //todo refactor
    private fun setFrame(target: Byte) {
        currentScreen = target
        toAccListButton.alpha = if (target == LIST) 1f else 0.3f
        toMapButton.alpha = if (target == MAP) 1f else 0.3f
        accListView.animate()
            .translationX((if (target == LIST) 0 else -displayWidth() * 2).toFloat())
        mapContainer.animate()
            .translationX((if (target == MAP) 0 else displayWidth() * 2).toFloat())
    }

    private fun redraw() {
        val newList = Content.getVisibleReversed()
            .asyncMap { AccidentRowFactory.make(this@MainScreenActivity, it) }

        runOnUiThread {
            listContent.removeAllViews()
            newList.forEach(listContent::addView)
            map.update()
        }
    }

    private fun setUpFeaturesAccessibility() = runOnUiThread {
        createAccButton.apply { if (!User.isReadOnly()) show() else hide() }
        dialButton.isEnabled = packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)
    }

    private fun setUpListeners() = listenersScope.launch {
        createAccButton.setOnClickListener { goTo(Screens.CREATE) }
        toAccListButton.setOnClickListener { setFrame(LIST) }
        toMapButton.setOnClickListener { setFrame(MAP) }
        dialButton.setOnClickListener { makeDial(getString(R.string.phone)) }
        bounceScrollView.setOverScrollListener { requestAccidents() }
    }

    private fun subscribe() = listenersScope.launch {
        SubscribeManager.subscribe(
            SubscribeManager.Event.LOCATION_UPDATED,
            SUBSCRIBE_TAG
        ) { updateStatusBar() }
        SubscribeManager.subscribe(
            SubscribeManager.Event.ACCIDENTS_UPDATED,
            SUBSCRIBE_TAG
        ) { listenersScope.launch { redraw() } }
    }

    private fun requestAccidents() {
        when {
            transaction -> return
            !MyApp.isOnline(this) -> Toast.makeText(
                this,
                getString(R.string.inet_not_available),
                Toast.LENGTH_LONG
            ).show()

            else -> {
                transaction = true
                progressBar.show()
                Content.requestUpdate { updateCompleteCallback() }
            }
        }
    }

    private fun updateCompleteCallback() = runOnUiThread {
        redraw()
        progressBar.hide()
        transaction = false
    }

    override fun onPause() {
        super.onPause()
        SubscribeManager.unSubscribeAll(SUBSCRIBE_TAG)
        Permissions.requestLocation(this) { MyLocationManager.sleep() }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.apply {
            when {
                hasExtra("toMap") -> toMap(intent.extras?.getInt("toMap", 0) ?: 0)
                hasExtra("toDetails") -> Unit //todo toDetails() ?
            }
            removeExtra("toMap")
            removeExtra("toDetails")
        }
        setIntent(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.small_settings_menu, menu)
        refreshItem = menu.findItem(R.id.action_refresh)
        if (transaction) refreshItem?.isVisible = false

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.small_menu_settings -> goTo(Screens.SETTINGS)
            R.id.small_menu_about -> goTo(Screens.ABOUT)
            R.id.action_refresh -> requestAccidents()
            R.id.do_not_disturb -> flipDoNotDisturb(item)
            else -> return false
        }
        return true
    }

    private fun flipDoNotDisturb(item: MenuItem) {
        item.setIcon(if (Preferences.doNotDisturb) R.drawable.ic_lock_ringer_on_alpha else R.drawable.ic_lock_ringer_off_alpha)
        Preferences.doNotDisturb = !Preferences.doNotDisturb
    }


    private fun toMap(id: Int) {
        setFrame(MAP)
        map.centerOnAccident(Content[id]!!)
    }

    private fun updateStatusBar() {
        val (part1, part2) = MyLocationManager.getAddress().carryOver()

        actionBar?.apply {
            title = part1
            subtitle = part2
        }
    }
}
