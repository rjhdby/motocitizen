package motocitizen.ui.menus

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.FragmentActivity
import motocitizen.content.accident.Accident
import motocitizen.main.R
import motocitizen.user.User
import motocitizen.utils.getAccidentTextToCopy
import motocitizen.utils.getPhonesFromText
import motocitizen.utils.gone
import motocitizen.utils.makeDial
import androidx.core.net.toUri

//todo refactor
class DetailsMenuController(val activity: FragmentActivity, val accident: Accident) {
    companion object {
        private const val SMS_MENU_MIN_ID = 100
        private const val SMS_MENU_MAX_ID = 200
        private const val CALL_MENU_MIN_ID = 400
        private const val CALL_MENU_MAX_ID = 500
        private val GENERAL_INFORMATION_VIEW = R.id.acc_details_general
        private val MENU = R.menu.menu_accident_details
    }

    enum class MenuAction {
        TO_MAP, HIDE_INFO, SHARE, SEND_HIDE_REQUEST, SEND_FINISH_REQUEST, NOTHING
    }

    private var generalLayout: View = activity.findViewById(GENERAL_INFORMATION_VIEW)

    private lateinit var mMenu: Menu

    //todo yobanyj pizdets!!!
    fun optionsMenuCreated(menu: Menu) {
        activity.menuInflater.inflate(MENU, menu)
        mMenu = menu
        //TODO Костыль
        val contactNumbers = accident.description.getPhonesFromText()
        if (contactNumbers.isEmpty()) return

        if (contactNumbers.size == 1) {
            mMenu.add(
                0,
                SMS_MENU_MIN_ID,
                0,
                activity.getString(R.string.send_sms) + contactNumbers[0]
            )
            mMenu.add(
                0,
                CALL_MENU_MIN_ID,
                0,
                activity.getString(R.string.make_call) + contactNumbers[0]
            )
        } else {
            val smsSub = mMenu.addSubMenu(activity.getString(R.string.send_sms))
            val callSub = mMenu.addSubMenu(activity.getString(R.string.make_call))
            contactNumbers.indices.forEach {
                smsSub.add(0, SMS_MENU_MIN_ID + it, 0, contactNumbers[it])
                callSub.add(0, CALL_MENU_MIN_ID + it, 0, contactNumbers[it])
            }
        }
    }

    //todo bloody hell
    fun itemSelected(item: MenuItem): MenuAction {
        if (item.itemId in SMS_MENU_MIN_ID..(SMS_MENU_MAX_ID - 1)) {
            val smsPrefix = activity.getString(R.string.send_sms)
            val phoneNumber = item.title.toString().substringAfter(smsPrefix)
            val smsIntent = Intent(Intent.ACTION_VIEW).apply {
                data = "smsto:$phoneNumber".toUri()
            }
            activity.startActivity(smsIntent)
        } else if (item.itemId in CALL_MENU_MIN_ID..(CALL_MENU_MAX_ID - 1)) {
            val callPrefix = activity.getString(R.string.make_call)
            activity.makeDial(item.title.toString().substringAfter(callPrefix))
        }

        when (item.itemId) {
            R.id.action_share -> {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, accident.getAccidentTextToCopy())
                    type = "text/plain"
                }
                activity.startActivity(
                    Intent.createChooser(
                        shareIntent,
                        activity.getString(R.string.share)
                    )
                )
            }

            R.id.action_hide_info, R.id.menu_hide_info -> hideMenuAction()
        }

        return when (item.itemId) {
            R.id.action_map, R.id.action_to_map -> MenuAction.TO_MAP
            R.id.action_share -> MenuAction.SHARE
            R.id.action_hide_info, R.id.menu_hide_info -> MenuAction.HIDE_INFO
            R.id.menu_acc_finish -> MenuAction.SEND_FINISH_REQUEST
            R.id.menu_acc_hide -> MenuAction.SEND_HIDE_REQUEST
            else -> MenuAction.NOTHING
        }
    }

    private fun hideMenuAction() = showGeneralLayout(
        when (generalLayout.visibility) {
            View.VISIBLE -> View.INVISIBLE
            else -> View.VISIBLE
        }
    )

    private fun showGeneralLayout(state: Int) {
        val menuItemActionHideInfo = mMenu.findItem(R.id.action_hide_info)
        val menuItemMenuHideInfo = mMenu.findItem(R.id.menu_hide_info)
        if (state == View.INVISIBLE) {
            generalLayout.gone()
            menuItemActionHideInfo.setIcon(R.drawable.ic_panel_down)
            menuItemMenuHideInfo.title = activity.getString(R.string.show_info_details)
        } else {
            generalLayout.visibility = View.VISIBLE
            menuItemActionHideInfo.setIcon(R.drawable.ic_panel_up)
            menuItemMenuHideInfo.title = activity.getString(R.string.hide_info_details)
        }
    }

    fun menuReconstruction() {
        mMenu.findItem(R.id.menu_acc_finish).apply {
            isVisible = User.isModerator()
            setTitle(if (accident.isEnded()) R.string.finish else R.string.unfinish)
        }
        mMenu.findItem(R.id.menu_acc_hide).apply {
            isVisible = User.isModerator()
            setTitle(if (accident.isHidden()) R.string.show else R.string.hide)
        }
    }
}