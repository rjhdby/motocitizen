package motocitizen.ui.dialogs.create

import afterTextChanged
import android.app.Activity
import android.view.ViewGroup
import android.widget.LinearLayout
import motocitizen.main.R
import org.jetbrains.anko.*

class EmptyAddressDialog(context: Activity, successCallback: (String) -> Unit) {
    private var text = ""

    init {
        context.alert {
            title = context.resources.getString(R.string.addressDialog)
            customView {
                verticalLayout {
                    layoutParams = LinearLayout.LayoutParams(matchParent, matchParent)
                    editText {
                        layoutParams = ViewGroup.LayoutParams(matchParent, wrapContent)
                        afterTextChanged { this@EmptyAddressDialog.text = text.toString().trim() }
                        hint = "Введите адрес"
                        setEms(10)
                    }
                }
            }
            positiveButton("Готово") { _ -> successCallback(text) }
            negativeButton("Отмена") { dialog -> dialog.cancel() }
        }.show()
    }
}