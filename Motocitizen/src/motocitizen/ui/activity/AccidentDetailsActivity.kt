package motocitizen.ui.activity

import android.app.Fragment
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupWindow
import android.widget.RadioGroup
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
import motocitizen.ui.fragments.DetailHistoryFragment
import motocitizen.ui.fragments.DetailMessagesFragment
import motocitizen.ui.fragments.DetailVolunteersFragment
import motocitizen.ui.frames.create.DetailsSummaryFrame
import motocitizen.ui.menus.AccidentContextMenu
import motocitizen.ui.menus.DetailsMenuController
import motocitizen.utils.bindView
import org.jetbrains.anko.startActivity

class AccidentDetailsActivity : AppCompatActivity() {
    companion object {
        const val ACCIDENT_ID_KEY = "id"
    }

    private val ROOT_LAYOUT = R.layout.activity_accident_details
    private val GENERAL_INFORMATION_VIEW = R.id.acc_details_general
    private val FRAGMENT_ROOT_VIEW = R.id.details_tab_content
    private val MESSAGE_TAB = R.id.details_tab_messages
    private val HISTORY_TAB = R.id.details_tab_history
    private val VOLUNTEER_TAB = R.id.details_tab_people

    private lateinit var accident: Accident
    private lateinit var summaryFrame: DetailsSummaryFrame
    private lateinit var menuController: DetailsMenuController

    private val tabs: RadioGroup by bindView(R.id.details_tabs_group)

    //todo exterminatus
    private var accNewState: AccidentStatus? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ROOT_LAYOUT)
        accident = Content[intent.extras.getInt(ACCIDENT_ID_KEY)]!!
        menuController = DetailsMenuController(this, accident)
        summaryFrame = DetailsSummaryFrame(this, accident)

        Content.requestDetailsForAccident(accident) { this.runOnUiThread { this.setupFragments() } }
        tabs.visibility = View.INVISIBLE
        update()
    }

    private fun setupFragments() {
        /*
        * Описание группы закладок внутри деталей происшествия
        */
        tabs.visibility = View.VISIBLE
        tabs.setOnCheckedChangeListener { group, _ ->
            fragmentManager
                    .beginTransaction()
                    .replace(FRAGMENT_ROOT_VIEW, selectFragment(group.checkedRadioButtonId))
                    .commit()
        }

        fragmentManager.beginTransaction().replace(FRAGMENT_ROOT_VIEW, selectFragment(VOLUNTEER_TAB)).commit()
    }

    private fun selectFragment(tabId: Int): Fragment = when (tabId) {
        MESSAGE_TAB   -> DetailMessagesFragment(accident)
        HISTORY_TAB   -> DetailHistoryFragment(accident)
        VOLUNTEER_TAB -> DetailVolunteersFragment(accident)
        else          -> DetailVolunteersFragment(accident)
    }

    override fun onResume() {
        super.onResume()
        findViewById(GENERAL_INFORMATION_VIEW).setOnLongClickListener { v ->
            val popupWindow: PopupWindow
            popupWindow = AccidentContextMenu(this@AccidentDetailsActivity, accident)
            val viewLocation = IntArray(2)
            v.getLocationOnScreen(viewLocation)
            popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, viewLocation[0], viewLocation[1])
            true
        }
        update()
    }

    fun update() {
        summaryFrame.update()
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menuController.optionsMenuCreated(menu)
        menuController.menuReconstruction()
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (menuController.itemSelected(item)) {
            DetailsMenuController.MenuAction.TO_MAP                                            -> jumpToMap()
            DetailsMenuController.MenuAction.HIDE_INFO, DetailsMenuController.MenuAction.SHARE -> Unit
            DetailsMenuController.MenuAction.SEND_HIDE_REQUEST                                 -> sendHideRequest()
            DetailsMenuController.MenuAction.SEND_FINISH_REQUEST                               -> sendFinishRequest()
            else                                                                               -> return false
        }
        return true
    }

    private fun sendFinishRequest() {
        //TODO Суперкостыль !!!
        if (accident.status === ENDED) {
            ActivateAccident(accident.id, this::accidentChangeCallback)
        } else {
            EndAccident(accident.id, this::accidentChangeCallback)
        }
    }

    private fun sendHideRequest() {
        //TODO какая то хуета
        accNewState = if (accident.status === ENDED) {
            ActivateAccident(accident.id, this::accidentChangeCallback)
            ACTIVE
        } else {
            HideAccident(accident.id, this::accidentChangeCallback)
            ENDED
        }
    }

    private fun accidentChangeCallback(result: ApiResponse) {
        if (result.hasError()) {
            //todo
        } else {
            //TODO Суперкостыль
            accident.status = accNewState!!
            update()
        }
    }

    fun jumpToMap() {
        startActivity<MainScreenActivity>("toMap" to accident.id)
    }
}
