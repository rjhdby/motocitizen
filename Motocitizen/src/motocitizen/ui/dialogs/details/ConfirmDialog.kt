package motocitizen.ui.dialogs.details

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class ConfirmDialog : DialogFragment() {
    companion object {
        private const val YES_STRING = android.R.string.yes
        private const val NO_STRING = android.R.string.no
        private const val ICON = android.R.drawable.ic_dialog_alert
    }

    var title: String? = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(title ?: "")
            .setIcon(ICON)
            .setPositiveButton(getString(YES_STRING)) { _, _ ->
                parentFragmentManager.setFragmentResult("confirm_dialog_result", Bundle().apply {
                    putBoolean("confirmed", true)
                })
            }
            .setNegativeButton(getString(NO_STRING)) { _, _ ->
                parentFragmentManager.setFragmentResult("confirm_dialog_result", Bundle().apply {
                    putBoolean("confirmed", false)
                })
            }
        return dialog.create()
    }
}
