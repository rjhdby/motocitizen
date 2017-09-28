package motocitizen.ui.popups

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.widget.*
import motocitizen.content.accident.Accident
import motocitizen.datasources.network.ApiResponse
import motocitizen.datasources.network.requests.ActivateAccident
import motocitizen.datasources.network.requests.BanRequest
import motocitizen.datasources.network.requests.EndAccident
import motocitizen.datasources.network.requests.HideAccident
import motocitizen.main.R
import motocitizen.router.Router
import motocitizen.utils.isEnded
import motocitizen.utils.isHidden
import motocitizen.utils.show

abstract class PopupWindowGeneral internal constructor(private val context: Context) : PopupWindow() {
    private val windowBackGround = ColorDrawable(0x00ffffff)
    private val contentColor = 0xFF202020.toInt()

    protected val layoutParams = TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    protected val rootView: TableLayout = TableLayout(context)

    init {
        rootView.orientation = LinearLayout.HORIZONTAL
        rootView.setBackgroundColor(contentColor)
        rootView.layoutParams = layoutParams
        contentView = rootView
        width = LayoutParams.WRAP_CONTENT
        height = LayoutParams.WRAP_CONTENT
        setBackgroundDrawable(windowBackGround)
        isOutsideTouchable = true
    }

    protected fun shareMessageView(textToShare: String) = makeButton(R.string.share, { shareButtonPressed(textToShare) })

    private fun shareButtonPressed(text: String) {
        Router.share(context as Activity, text)
        dismiss()
    }

    protected fun copyButtonView(text: String) = makeButton(R.string.copy, { copyButtonPressed(text) })

    private fun copyButtonPressed(text: String) {
        val myClipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        myClipboard.primaryClip = ClipData.newPlainText("text", text)
        dismiss()
    }

    protected fun phoneButtonView(phone: String) = makeButton(context.getString(R.string.popup_dial, phone), { phoneButtonPressed(phone) })

    private fun phoneButtonPressed(phone: String) {
        Router.dial(context as Activity, phone)
        dismiss()
    }

    protected fun smsButtonView(phone: String) = makeButton(context.getString(R.string.popup_sms, phone), { smsButtonPressed(phone) })

    private fun smsButtonPressed(phone: String) {
        Router.sms(context as Activity, phone)
        dismiss()
    }

    protected fun finishButtonView(point: Accident) = makeButton(if (point.isEnded()) R.string.unfinish else R.string.finish, { finishButtonPressed(point) })

    private fun finishButtonPressed(point: Accident) {
        if (point.isEnded()) {
            ActivateAccident(point.id) { }
        } else {
            EndAccident(point.id) { }
        }
        dismiss()
    }

    protected fun hideButtonView(point: Accident) = makeButton(if (point.isHidden()) R.string.show else R.string.hide, { hideButtonPressed(point) })

    private fun hideButtonPressed(point: Accident) {
        if (point.isHidden()) {
            ActivateAccident(point.id) { }
        } else {
            HideAccident(point.id) { }
        }
        dismiss()
    }

    protected fun coordinatesButtonView(point: Accident): TableRow = makeButton(R.string.copy_coordinates, { coordinatesButtonPressed(point) })

    private fun coordinatesButtonPressed(point: Accident) {
        val latLng = point.coordinates
        val myClipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        myClipboard.primaryClip = ClipData.newPlainText("text", String.format("%s,%s", latLng.latitude, latLng.longitude))
        show(context, context.getString(R.string.coordinates_copied))
        dismiss()
    }

    protected fun banButtonView(id: Int): TableRow = makeButton("Забанить", { banButtonPressed(id) })

    private fun banButtonPressed(id: Int) {
        BanRequest(id, this::banRequestCallback)
        dismiss()
    }

    private fun banRequestCallback(response: ApiResponse) {
        (context as Activity).runOnUiThread {
            show(context, if (response.hasError())
                "Ошибка связи с сервером"
            else
                "Пользователь забанен")
        }
    }

    private fun makeButton(text: String, listener: (View) -> Unit): TableRow {
        val button = Button(context)
        button.text = text
        button.setOnClickListener(listener)
        val tableRow = TableRow(context)
        tableRow.addView(button, layoutParams)
        return tableRow
    }

    private fun makeButton(text: Int, listener: (View) -> Unit): TableRow = makeButton(context.getString(text), listener)
}
