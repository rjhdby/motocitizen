package motocitizen.ui.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import motocitizen.MyApp
import motocitizen.content.Content
import motocitizen.datasources.preferences.Preferences
import motocitizen.geo.geolocation.MyLocationManager
import motocitizen.geo.maps.MainMapManager
import motocitizen.main.R
import motocitizen.permissions.Permissions
import motocitizen.router.Router
import motocitizen.router.SubscribeManager
import motocitizen.ui.changelog.ChangeLog
import motocitizen.ui.rows.accident.AccidentRowFactory
import motocitizen.ui.views.BounceScrollView
import motocitizen.user.User
import motocitizen.utils.asyncMap
import motocitizen.utils.bindView
import motocitizen.utils.displayWidth

class MainScreenActivity : AppCompatActivity() {
    companion object {
        private const val LIST: Byte = 0
        private const val MAP: Byte = 1
        private const val SUBSCRIBE_TAG = "mainScreen"
    }

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
    private var inTransaction = false
    private var currentScreen = LIST

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_screen_activity)
        map = MainMapManager(this)
        showChangeLogIfUpdated()
    }

    override fun onResume() {
        super.onResume()

        runBlocking {
            showCurrentFrame()
            setUpFeaturesAccessibility()
            setUpListeners()
            subscribe()
            val redraw = redraw()
            async {
                MyLocationManager.wakeup(this@MainScreenActivity)
            }
            redraw.await()
            requestAccidents()
        }

    }

    private fun subscribe() = async {
        SubscribeManager.subscribe(SubscribeManager.Event.LOCATION_UPDATED, SUBSCRIBE_TAG) { updateStatusBar() }
        SubscribeManager.subscribe(SubscribeManager.Event.ACCIDENTS_UPDATED, SUBSCRIBE_TAG) { redraw() }
    }

    private fun showChangeLogIfUpdated() {
        if (!MyApp.firstStart) return
        ChangeLog.getDialog(this).show()
        MyApp.firstStart = false
    }

    private fun setUpListeners() = async {
        createAccButton.setOnClickListener { Router.goTo(this@MainScreenActivity, Router.Target.CREATE) }
        toAccListButton.setOnClickListener { showListFrame() }
        toMapButton.setOnClickListener { showMapFrame() }
        dialButton.setOnClickListener { Router.dial(this@MainScreenActivity, getString(R.string.phone)) }
        bounceScrollView.setOverScrollListener { requestAccidents() }
    }

    override fun onPause() {
        super.onPause()
        SubscribeManager.unSubscribeAll(SUBSCRIBE_TAG)
        Permissions.requestLocation(this) { MyLocationManager.sleep() }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.hasExtra("toMap")) {
            toMap(intent.extras.getInt("toMap", 0))
            intent.removeExtra("toMap")
        }
        if (intent.hasExtra("toDetails")) {
            intent.removeExtra("toDetails")
        }
        setIntent(intent)
    }

    private fun setUpFeaturesAccessibility() = async {
        createAccButton.visibility = if (User.isStandard) View.VISIBLE else View.INVISIBLE
        dialButton.isEnabled = packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)
    }

    private fun redraw() = async {
        val newList = Content.getVisibleReversed().asyncMap { AccidentRowFactory.make(this@MainScreenActivity, it) }

        runOnUiThread {
            listContent.removeAllViews()
            newList.forEach(listContent::addView)
            map.update()
        }
    }

    private fun requestAccidents() {
        if (inTransaction) return
        if (MyApp.isOnline(this)) {
            startRefreshAnimation()
            Content.requestUpdate { updateCompleteCallback() }
        } else {
            Toast.makeText(this, getString(R.string.inet_not_available), Toast.LENGTH_LONG).show()
        }
    }

    private fun updateCompleteCallback() {
        runOnUiThread {
            stopRefreshAnimation()
            redraw()
        }
    }

    //todo extract progressBar to separate class
    private fun stopRefreshAnimation() {
        setRefreshAnimation(false)
    }

    private fun startRefreshAnimation() {
        setRefreshAnimation(true)
    }

    private fun setRefreshAnimation(status: Boolean) {
        progressBar.visibility = if (status) View.VISIBLE else View.INVISIBLE
        inTransaction = status
        //TODO костыль
        if (refreshItem != null) refreshItem!!.isVisible = !status
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.small_settings_menu, menu)
        refreshItem = menu.findItem(R.id.action_refresh)
        if (inTransaction) refreshItem!!.isVisible = false

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.small_menu_refresh  -> requestAccidents()
            R.id.small_menu_settings -> Router.goTo(this, Router.Target.SETTINGS)
            R.id.small_menu_about    -> Router.goTo(this, Router.Target.ABOUT)
            R.id.action_refresh      -> requestAccidents()
            R.id.do_not_disturb      -> {
                item.setIcon(if (Preferences.doNotDisturb) R.drawable.ic_lock_ringer_on_alpha else R.drawable.ic_lock_ringer_off_alpha)
                Preferences.doNotDisturb = !Preferences.doNotDisturb
            }
            else                     -> return false
        }
        return true
    }

    private fun showListFrame() {
        setFrame(LIST)
    }

    private fun showMapFrame() {
        setFrame(MAP)
    }

    private fun showCurrentFrame() {
        setFrame(currentScreen)
    }

    private fun setFrame(target: Byte) {
        currentScreen = target
        toAccListButton.alpha = if (target == LIST) 1f else 0.3f
        toMapButton.alpha = if (target == MAP) 1f else 0.3f
        accListView.animate().translationX((if (target == LIST) 0 else -displayWidth() * 2).toFloat())
        mapContainer.animate().translationX((if (target == MAP) 0 else displayWidth() * 2).toFloat())
    }

    private fun toMap(id: Int) {
        showMapFrame()
        map.centerOnAccident(Content[id]!!)
    }

    //todo refactor
    private fun updateStatusBar() {
        var address = MyLocationManager.getAddress()
        var subTitle = ""
        //Делим примерно пополам, учитывая пробел или запятую
        val commaPos = address.lastIndexOf(",", address.length / 2)
        val spacePos = address.lastIndexOf(" ", address.length / 2)

        if (commaPos != -1 || spacePos != -1) {
            subTitle = address.substring(Math.max(commaPos, spacePos) + 1)
            address = address.substring(0, Math.max(commaPos, spacePos))
        }

        actionBar?.title = address
        if (!subTitle.isEmpty()) actionBar?.subtitle = subTitle
    }
}
