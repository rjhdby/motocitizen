package motocitizen.ui.menus

import android.app.Activity
import android.content.Context
import motocitizen.content.accident.Accident
import motocitizen.datasources.network.ApiResponse
import motocitizen.datasources.network.requests.ActivateAccident
import motocitizen.datasources.network.requests.BanRequest
import motocitizen.datasources.network.requests.EndAccident
import motocitizen.datasources.network.requests.HideAccident
import motocitizen.datasources.preferences.Preferences
import motocitizen.main.R
import motocitizen.user.User
import motocitizen.utils.*
import org.jetbrains.anko.makeCall
import org.jetbrains.anko.sendSMS
import org.jetbrains.anko.share

class AccidentContextMenu(context: Context, val accident: Accident) : ContextMenu(context) {
    init {
        addCommonMenu(context)
        addOwnerAndModeratorMenu()
        addModeratorMenu()
    }

    private fun addOwnerAndModeratorMenu() {
        if (!User.isModerator && Preferences.login != accident.owner.name()) return
        addButton(if (accident.isEnded()) R.string.unfinish else R.string.finish, this::finishButtonPressed)
    }

    private fun addCommonMenu(context: Context) {
        addButton(R.string.share) { context.share(accident.getAccidentTextToCopy()) }
        addButton(R.string.copy) { context.copyToClipBoard(accident.getAccidentTextToCopy()) }
        accident.description.getPhonesFromText().forEach {
            addButton(context.getString(R.string.popup_dial, it)) { context.makeCall(it) }
        }
        accident.description.getPhonesFromText().forEach {
            addButton(context.getString(R.string.popup_sms, it)) { context.sendSMS(it) }
        }
        addButton(R.string.copy_coordinates) { context.copyToClipBoard(String.format("%s,%s", accident.latitude, accident.longitude)) }
    }

    private fun addModeratorMenu() {
        if (!User.isModerator) return
        addButton(if (accident.isHidden()) R.string.show else R.string.hide, this::hideButtonPressed)
        addButton("Забанить") { BanRequest(accident.owner, this::banRequestCallback) }
    }

    private fun finishButtonPressed() {
        when {
            accident.isEnded() -> ActivateAccident(accident.id) { }
            else               -> EndAccident(accident.id) { }
        }

    }

    private fun hideButtonPressed() {
        when {
            accident.isHidden() -> ActivateAccident(accident.id) { }
            else                -> HideAccident(accident.id) { }
        }
    }

    private fun banRequestCallback(response: ApiResponse) {
        (context as Activity).runOnUiThread {
            context.showToast(if (response.hasError())
                                  "Ошибка связи с сервером"
                              else
                                  "Пользователь забанен")
        }
    }
}