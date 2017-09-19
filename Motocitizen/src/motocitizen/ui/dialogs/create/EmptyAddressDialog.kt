package motocitizen.ui.dialogs.create

import android.app.Activity
import android.app.AlertDialog
import android.widget.EditText
import motocitizen.main.R

class EmptyAddressDialog(context: Activity, successCallback: (String) -> Unit) : AlertDialog.Builder(context) {
    private val DIALOG_LAYOUT = R.layout.dialog

    init {
        setTitle(R.string.addressDialog)
        val linearLayout = context.layoutInflater.inflate(DIALOG_LAYOUT, null)
        setView(linearLayout)
        val addressEditText = linearLayout.findViewById(R.id.address_edit_Text) as EditText
        setPositiveButton("Готово", { _, _ -> successCallback(addressEditText.text.toString().trim()) })
        setNegativeButton("Отмена", { dialog, _ -> dialog.cancel() })
        create()
        show()
    }
}