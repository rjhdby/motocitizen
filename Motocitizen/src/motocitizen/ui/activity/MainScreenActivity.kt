package motocitizen.ui.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import motocitizen.MyApp
import motocitizen.content.Content
import motocitizen.datasources.preferences.Preferences
import motocitizen.geo.geolocation.MyLocationManager
import motocitizen.geo.maps.MainMapManager
import motocitizen.main.R
import motocitizen.permissions.Permissions
import motocitizen.router.Router
import motocitizen.ui.changelog.ChangeLog
import motocitizen.ui.rows.accident.AccidentRowFactory
import motocitizen.ui.views.BounceScrollView
import motocitizen.user.User
import motocitizen.utils.getWidth

class MainScreenActivity : AppCompatActivity() {
    private val LIST: Byte = 0
    private val MAP: Byte = 1
    private lateinit var actionBar: ActionBar

    private lateinit var mapContainer: ViewGroup
    private lateinit var createAccButton: ImageButton
    private lateinit var toAccListButton: ImageButton
    private lateinit var toMapButton: ImageButton
    private lateinit var accListView: View
    private lateinit var progressBar: ProgressBar
    private lateinit var listContent: ViewGroup

    private var refreshItem: MenuItem? = null
    private lateinit var map: MainMapManager
    private var inTransaction = false
    private var currentScreen = LIST

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_screen_activity)
        actionBar = supportActionBar!!  //todo NPE
        map = MainMapManager(this)
    }

    override fun onResume() {
        super.onResume()
        wakeUpLocationUpdate()
        showChangeLogIfUpdated()
        MyLocationManager.subscribeToLocationUpdate("MAIN", this::updateStatusBar)
        disableDialOnTablets()

        bindViews()
        setUpListeners()

        setPermissions()
        showCurrentFrame()
        redraw()
        accidents
    }

    private fun wakeUpLocationUpdate() {
        Permissions.requestLocation(this, {
            MyLocationManager.wakeup()
            map.enableLocation()
        }) { }
    }

    private fun disableDialOnTablets() {
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
            this.findViewById(R.id.dial_button).isEnabled = false
        }
    }

    private fun showChangeLogIfUpdated() {
        if (!Preferences.newVersion) return
        val changeLogDlg = ChangeLog.getDialog(this)
        changeLogDlg.show()
        Preferences.newVersion = false
    }

    private fun setUpListeners() {
        createAccButton.setOnClickListener { Router.goTo(this, Router.Target.CREATE) }
        toAccListButton.setOnClickListener { showListFrame() }
        toMapButton.setOnClickListener { showMapFrame() }
        this.findViewById(R.id.dial_button).setOnClickListener { Router.dial(this, getString(R.string.phone)) }
        (this.findViewById(R.id.accListRefresh) as BounceScrollView).setOverScrollListener { accidents }
    }

    private fun bindViews() {
        accListView = this.findViewById(R.id.acc_list)
        progressBar = this.findViewById(R.id.progressBar) as ProgressBar
        mapContainer = this.findViewById(R.id.google_map) as ViewGroup
        createAccButton = this.findViewById(R.id.add_point_button) as ImageButton
        toAccListButton = this.findViewById(R.id.list_button) as ImageButton
        toMapButton = this.findViewById(R.id.map_button) as ImageButton
        listContent = this.findViewById(R.id.accListContent) as ViewGroup
    }

    override fun onPause() {
        super.onPause()
        MyLocationManager.unSubscribe("MAIN")
        Permissions.requestLocation(this, { MyLocationManager.sleep() }) { }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.hasExtra("toMap")) {
            //mainFragment.toMap(intent.getExtras().getInt("toMap", 0));
            toMap(intent.extras.getInt("toMap", 0))
            intent.removeExtra("toMap")
        }
        if (intent.hasExtra("toDetails")) {
            intent.removeExtra("toDetails")
        }
        setIntent(intent)
    }

    private fun setPermissions() {
        createAccButton.visibility = if (User.isStandard) View.VISIBLE else View.INVISIBLE
    }

    private fun redraw() {
        val newList = Content.getVisibleReversed().map { AccidentRowFactory.make(this, it) }
        listContent.removeAllViews()

        for (row in newList) {
            listContent.addView(row)
        }
        map.addContent()
    }

    private val accidents: Unit
        get() {
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
            R.id.small_menu_refresh  -> accidents
            R.id.small_menu_settings -> Router.goTo(this, Router.Target.SETTINGS)
            R.id.small_menu_about    -> Router.goTo(this, Router.Target.ABOUT)
            R.id.action_refresh      -> accidents
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
        accListView.animate().translationX((if (target == LIST) 0 else -getWidth(this) * 2).toFloat())
        mapContainer.animate().translationX((if (target == MAP) 0 else getWidth(this) * 2).toFloat())
    }

    private fun toMap(id: Int) {
        showMapFrame()
        map.jumpToPoint(Content.accident(id).coordinates)
    }

    private fun updateStatusBar(latLng: LatLng) {
        var address = MyLocationManager.getAddress(latLng)
        var subTitle = ""
        //Делим примерно пополам, учитывая пробел или запятую
        val commaPos = address.lastIndexOf(",", address.length / 2)
        val spacePos = address.lastIndexOf(" ", address.length / 2)

        if (commaPos != -1 || spacePos != -1) {
            subTitle = address.substring(Math.max(commaPos, spacePos) + 1)
            address = address.substring(0, Math.max(commaPos, spacePos))
        }

        actionBar.title = address
        if (!subTitle.isEmpty()) actionBar.subtitle = subTitle
    }
}
