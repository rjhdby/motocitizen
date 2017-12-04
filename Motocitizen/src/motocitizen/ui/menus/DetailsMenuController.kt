package motocitizen.ui.menus

import android.support.v4.app.FragmentActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import motocitizen.content.accident.Accident
import motocitizen.dictionary.AccidentStatus
import motocitizen.main.R
import motocitizen.router.Router
import motocitizen.user.User
import motocitizen.utils.getAccidentTextToCopy
import motocitizen.utils.getPhonesFromText

//todo refactor
class DetailsMenuController(val activity: FragmentActivity, val accident: Accident) {
    enum class MenuAction {
        TO_MAP, HIDE_INFO, SHARE, SEND_HIDE_REQUEST, SEND_FINISH_REQUEST, NOTHING
    }

    private val GENERAL_INFORMATION_VIEW = R.id.acc_details_general
    private val MENU = R.menu.menu_accident_details
    private val SMS_MENU_MIN_ID = 100
    private val SMS_MENU_MAX_ID = 200
    private val CALL_MENU_MIN_ID = 400
    private val CALL_MENU_MAX_ID = 500

    private var generalLayout: View = activity.findViewById(GENERAL_INFORMATION_VIEW)

    lateinit var mMenu: Menu

    //todo yobanyj pizdets!!!
    fun optionsMenuCreated(menu: Menu) {
        activity.menuInflater.inflate(MENU, menu)
        mMenu = menu
        //TODO Костыль
        val contactNumbers = accident.description.getPhonesFromText()
        if (contactNumbers.isEmpty()) return
        if (contactNumbers.size == 1) {
            mMenu.add(0, SMS_MENU_MIN_ID, 0, activity.getString(R.string.send_sms) + contactNumbers[0])
            mMenu.add(0, CALL_MENU_MIN_ID, 0, activity.getString(R.string.make_call) + contactNumbers[0])
        } else {
            val smsSub = mMenu.addSubMenu(activity.getString(R.string.send_sms))
            val callSub = mMenu.addSubMenu(activity.getString(R.string.make_call))
            for (i in contactNumbers.indices) {
                smsSub.add(0, SMS_MENU_MIN_ID + i, 0, contactNumbers[i])
                callSub.add(0, CALL_MENU_MIN_ID + i, 0, contactNumbers[i])
            }
        }
    }
//todo bloody hell
    fun itemSelected(item: MenuItem): MenuAction {
        if (item.itemId in SMS_MENU_MIN_ID..(SMS_MENU_MAX_ID - 1)) {
            val smsPrefix = activity.getString(R.string.send_sms)
            var number = item.title as String
            if (number.contains(smsPrefix))
                number = number.substring(smsPrefix.length, number.length)
            Router.sms(activity, number)
        } else if (item.itemId in CALL_MENU_MIN_ID..(CALL_MENU_MAX_ID - 1)) {
            val callPrefix = activity.getString(R.string.make_call)
            var number = item.title as String
            if (number.contains(callPrefix))
                number = number.substring(callPrefix.length, number.length)
            Router.dial(activity, number)
        }

        when (item.itemId) {
            R.id.action_share                          -> Router.share(activity, accident.getAccidentTextToCopy())
            R.id.action_hide_info, R.id.menu_hide_info -> hideMenuAction()
        }

        return when (item.itemId) {
            R.id.action_map, R.id.action_to_map        -> MenuAction.TO_MAP
            R.id.action_share                          -> MenuAction.SHARE
            R.id.action_hide_info, R.id.menu_hide_info -> MenuAction.HIDE_INFO
            R.id.menu_acc_finish                       -> MenuAction.SEND_FINISH_REQUEST
            R.id.menu_acc_hide                         -> MenuAction.SEND_HIDE_REQUEST
            else                                       -> MenuAction.NOTHING
        }
    }

    private fun hideMenuAction() {
        showGeneralLayout(when (generalLayout.visibility) {
                              View.VISIBLE -> View.INVISIBLE
                              else         -> View.VISIBLE
                          })
    }

    private fun showGeneralLayout(state: Int) {
        val menuItemActionHideInfo = mMenu.findItem(R.id.action_hide_info)
        val menuItemMenuHideInfo = mMenu.findItem(R.id.menu_hide_info)
        if (state == View.INVISIBLE) {
            generalLayout.visibility = View.GONE
            menuItemActionHideInfo.setIcon(R.drawable.ic_panel_down)
            menuItemMenuHideInfo.title = activity.getString(R.string.show_info_details)
        } else {
            generalLayout.visibility = View.VISIBLE
            menuItemActionHideInfo.setIcon(R.drawable.ic_panel_up)
            menuItemMenuHideInfo.title = activity.getString(R.string.hide_info_details)
        }
    }

    //todo smell
    fun menuReconstruction() {
        val finish = mMenu.findItem(R.id.menu_acc_finish)
        val hide = mMenu.findItem(R.id.menu_acc_hide)
        finish.isVisible = User.isModerator
        hide.isVisible = User.isModerator
        finish.setTitle(R.string.finish)
        hide.setTitle(R.string.hide)
        when (accident.status) {
            AccidentStatus.ENDED  -> finish.setTitle(R.string.unfinish)
            AccidentStatus.HIDDEN -> hide.setTitle(R.string.show)
            else                  -> Unit
        }
    }
}