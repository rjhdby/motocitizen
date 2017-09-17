package motocitizen.utils.popups

import android.content.Context
import android.widget.PopupWindow
import motocitizen.content.accident.Accident
import motocitizen.dictionary.Medicine
import motocitizen.user.User
import motocitizen.utils.Preferences
import motocitizen.utils.dateTimeString
import motocitizen.utils.getAccidentTextToCopy
import motocitizen.utils.getPhonesFromText

//todo renew after hide/end/activate

class AccidentListPopup(val context: Context, private val accident: Accident) : PopupWindowGeneral(context) {
    fun getPopupWindow(context: Context): PopupWindow {
        val accText = accident.getAccidentTextToCopy()

        content.addView(copyButtonRow(context, accText))
        for (phone in getPhonesFromText(accident.description)) {
            content.addView(phoneButtonRow(context, phone), layoutParams)
            content.addView(smsButtonRow(context, phone), layoutParams)
        }
        if (User.isModerator || Preferences.login == accident.ownerName())
            content.addView(finishButtonRow(accident))

        if (User.isModerator) {
            content.addView(hideButtonRow(accident))
            content.addView(banButtonRow(context, accident.owner), layoutParams)
        }

        content.addView(shareMessage(context, accText))
        content.addView(coordinatesButtonRow(context, accident), layoutParams)
        popupWindow.contentView = content
        return popupWindow
    }
}
