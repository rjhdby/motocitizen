package motocitizen.ui.menus

import android.app.Activity
import android.content.Context
import motocitizen.content.accident.Accident
import motocitizen.datasources.network.ApiResponse
import motocitizen.datasources.network.requests.ActivateAccident
import motocitizen.datasources.network.requests.BanRequest
import motocitizen.datasources.network.requests.EndAccident
import motocitizen.datasources.network.requests.HideAccident
import motocitizen.dictionary.AccidentStatus
import motocitizen.main.R
import motocitizen.subscribe.SubscribeManager
import motocitizen.user.User
import motocitizen.utils.*
import org.jetbrains.anko.sendSMS
import org.jetbrains.anko.share

class AccidentContextMenu(context: Context, val accident: Accident) : ContextMenu(context) {
    init {
        addCommonMenu()
        addOwnerAndModeratorMenu()
        addModeratorMenu()
    }

    private fun addOwnerAndModeratorMenu() {
        if (User.notIsModerator() && User.id != accident.owner) return
        addButton(if (accident.isEnded()) R.string.unfinish else R.string.finish) { finishButtonPressed() }
    }

    private fun addCommonMenu() {
        addButton(R.string.share) { context.share(accident.getAccidentTextToCopy()) }
        addButton(R.string.copy) { context.copyToClipBoard(accident.getAccidentTextToCopy()) }
        accident.description.getPhonesFromText().forEach {
            addButton(context.getString(R.string.popup_dial, it)) { context.makeDial(it) }
        }
        accident.description.getPhonesFromText().forEach {
            addButton(context.getString(R.string.popup_sms, it)) { context.sendSMS(it) }
        }
        addButton(R.string.copy_coordinates) { context.copyToClipBoard(String.format("%s,%s", accident.latitude, accident.longitude)) }
    }

    private fun addModeratorMenu() {
        if (User.notIsModerator()) return
        addButton(if (accident.isHidden()) R.string.show else R.string.hide) { hideButtonPressed() }
        addButton("Забанить") { BanRequest(accident.owner, ::banRequestCallback).call() }
    }

    private fun finishButtonPressed() = when {
        accident.isEnded() -> ActivateAccident(accident.id) {
            accident.status = AccidentStatus.ACTIVE
            SubscribeManager.fireEvent(SubscribeManager.Event.ACCIDENTS_UPDATED)
        }
        else               -> EndAccident(accident.id) {
            accident.status = AccidentStatus.ENDED
            SubscribeManager.fireEvent(SubscribeManager.Event.ACCIDENTS_UPDATED)
        }
    }.call()

    private fun hideButtonPressed() = when {
        accident.isHidden() -> ActivateAccident(accident.id) {
            accident.status = AccidentStatus.ACTIVE
            SubscribeManager.fireEvent(SubscribeManager.Event.ACCIDENTS_UPDATED)
        }
        else                -> HideAccident(accident.id) {
            accident.status = AccidentStatus.HIDDEN
            SubscribeManager.fireEvent(SubscribeManager.Event.ACCIDENTS_UPDATED)
        }
    }.call()

    private fun banRequestCallback(response: ApiResponse) {
        val result = when {
            response.hasError() -> "Ошибка связи с сервером"
            else                -> "Пользователь забанен"
        }
        (context as Activity).runOnUiThread { context.showToast(result) }
    }
}