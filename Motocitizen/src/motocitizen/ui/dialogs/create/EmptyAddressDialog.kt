package motocitizen.ui.dialogs.create

import afterTextChanged
import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import motocitizen.main.R

class EmptyAddressDialog(
    context: Activity,
    address: String,
    successCallback: (String) -> Unit
) {
    private var thisText = ""

    init {
        val editText = EditText(context).apply {
            setText(address)
            hint = "Введите адрес"
            setEms(10)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    thisText = s?.toString()?.trim().orEmpty()
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }

        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 20, 40, 0)
            addView(editText)
        }

        AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.addressDialog))
            .setView(layout)
            .setPositiveButton("Готово") { _, _ ->
                successCallback(thisText)
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }
}