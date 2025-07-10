package motocitizen.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import motocitizen.content.Content
import motocitizen.content.accident.Accident
import motocitizen.datasources.network.ApiResponse
import motocitizen.datasources.network.requests.ActivateAccident
import motocitizen.datasources.network.requests.EndAccident
import motocitizen.datasources.network.requests.HideAccident
import motocitizen.dictionary.AccidentStatus
import motocitizen.dictionary.AccidentStatus.ACTIVE
import motocitizen.dictionary.AccidentStatus.ENDED
import motocitizen.main.R
import motocitizen.subscribe.SubscribeManager
import motocitizen.ui.Screens
import motocitizen.ui.fragments.DetailHistoryFragment
import motocitizen.ui.fragments.DetailMessagesFragment
import motocitizen.ui.fragments.DetailVolunteersFragment
import motocitizen.ui.frames.create.DetailsSummaryFrame
import motocitizen.ui.menus.AccidentContextMenu
import motocitizen.ui.menus.DetailsMenuController
import motocitizen.ui.rows.AccidentRow
import motocitizen.utils.bindView
import motocitizen.utils.changeFragmentTo
import motocitizen.utils.goTo
import motocitizen.utils.hide
import motocitizen.utils.obtainActionBarHeight
import motocitizen.utils.show

class AccidentDetailsActivity : AppCompatActivity() {
    companion object {
        const val ACCIDENT_ID_KEY = "id"
        private val ROOT_LAYOUT = R.layout.activity_accident_details
        private val GENERAL_INFORMATION_VIEW = R.id.acc_details_general
        private val FRAGMENT_ROOT_VIEW = R.id.details_tab_content
    }

    enum class Tab(val id: Int) {
        MESSAGE_TAB(R.id.details_tab_messages),
        HISTORY_TAB(R.id.details_tab_history),
        VOLUNTEER_TAB(R.id.details_tab_people);

        companion object {
            fun byId(id: Int) = entries.firstOrNull { it.id == id } ?: VOLUNTEER_TAB
        }
        fun fragment(accident: Accident): Fragment = when (this) {
            MESSAGE_TAB -> DetailMessagesFragment()
            HISTORY_TAB -> DetailHistoryFragment()
            VOLUNTEER_TAB -> DetailVolunteersFragment()
        }.apply {
            setAccident(accident)
        }
    }

    private lateinit var accident: Accident
    private lateinit var summaryFrame: DetailsSummaryFrame
    private lateinit var menuController: DetailsMenuController

    private val tabs: RadioGroup by bindView(R.id.details_tabs_group)

    //todo exterminatus
    private var accNewState: AccidentStatus? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ROOT_LAYOUT)
        val root = findViewById<ViewGroup>(R.id.activity_accident_details)
        val actionBarPadding = obtainActionBarHeight()
        root.setPadding(0, actionBarPadding, 0, 0)

        init(savedInstanceState?.getInt(ACCIDENT_ID_KEY))
    }

    private fun init(savedId: Int? = null) {
        try {
            val id = savedId ?: intent.extras?.getInt(ACCIDENT_ID_KEY) ?: throw RuntimeException()
            accident = Content[id] ?: throw RuntimeException()
            val row = AccidentRow.make(this, accident)
            menuController = DetailsMenuController(this, accident)
            summaryFrame = DetailsSummaryFrame(this, accident)

            Content.requestDetailsForAccident(accident) { setupFragments() }
            tabs.hide()
            update()
        } catch (e: Exception) {
            goTo(Screens.MAIN)
        }

    }

    private fun setupFragments() = runOnUiThread {
        tabs.show()
        showFragment(Tab.VOLUNTEER_TAB)
        tabs.setOnCheckedChangeListener { group, _ ->
            showFragment(Tab.byId(group.checkedRadioButtonId))
        }
    }

    private fun showFragment(tab: Tab) = changeFragmentTo(FRAGMENT_ROOT_VIEW, tab.fragment(accident))

    override fun onResume() {
        super.onResume()
        findViewById<View>(GENERAL_INFORMATION_VIEW)
            .setOnLongClickListener(this@AccidentDetailsActivity::generalPopUpListener)
        init()
    }

    private fun generalPopUpListener(view: View): Boolean {
        AccidentContextMenu(this, accident).showAsDropDown(view)
        return true
    }

    fun update() {
        summaryFrame.update()
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        try {
            menuController.optionsMenuCreated(menu)
            menuController.menuReconstruction()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            return super.onPrepareOptionsMenu(menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (menuController.itemSelected(item)) {
            DetailsMenuController.MenuAction.TO_MAP -> toMap()
            DetailsMenuController.MenuAction.HIDE_INFO, DetailsMenuController.MenuAction.SHARE -> Unit
            DetailsMenuController.MenuAction.SEND_HIDE_REQUEST -> sendHideRequest()
            DetailsMenuController.MenuAction.SEND_FINISH_REQUEST -> sendFinishRequest()
            else -> return false
        }
        return true
    }

    private fun sendFinishRequest() {
        //TODO Суперкостыль !!!
        when (accident.status) {
            ENDED -> ActivateAccident(accident.id, this::accidentChangeCallback).call()
            else -> EndAccident(accident.id, this::accidentChangeCallback).call()
        }
    }

    private fun sendHideRequest() {
        //TODO какая то хуета
        accNewState = when (accident.status) {
            ENDED -> {
                ActivateAccident(accident.id, this::accidentChangeCallback).call()
                ACTIVE
            }

            else -> {
                HideAccident(accident.id, this::accidentChangeCallback).call()
                ENDED
            }
        }
    }

    private fun accidentChangeCallback(result: ApiResponse) {
        if (result.hasError()) {
            //todo
        } else {
            SubscribeManager.fireEvent(SubscribeManager.Event.ACCIDENTS_UPDATED)
            //TODO Суперкостыль
            accident.status = accNewState!!
            update()
        }
    }

    fun toMap() = goTo(Screens.MAIN, mapOf("toMap" to accident.id))

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.hasExtra(ACCIDENT_ID_KEY)) {
            val cached = Content[intent.getIntExtra(ACCIDENT_ID_KEY, 0)]
            if (cached == null) {
                goTo(Screens.MAIN)
            } else {
                accident = cached
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(ACCIDENT_ID_KEY, accident.id)
    }
}
