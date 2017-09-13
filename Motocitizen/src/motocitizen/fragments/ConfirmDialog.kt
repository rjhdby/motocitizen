package motocitizen.fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.res.Resources
import android.os.Bundle

class ConfirmDialog() : DialogFragment() {
    private val YES_STRING = android.R.string.yes
    private val NO_STRING = android.R.string.no
    private val ICON = android.R.drawable.ic_dialog_alert

    private lateinit var title: String

    constructor(title: String) : this() {
        this.title = title
    }

    override fun onCreateDialog(savedInstanceState: Bundle): Dialog {
        val dialog = AlertDialog.Builder(activity)
        dialog.setTitle(title)
        dialog.setIcon(ICON)
        dialog.setPositiveButton(Resources.getSystem().getString(YES_STRING)) { _, _ -> targetFragment.onActivityResult(targetRequestCode, Activity.RESULT_OK, activity.intent) }
        dialog.setNegativeButton(Resources.getSystem().getString(NO_STRING)) { _, _ -> targetFragment.onActivityResult(targetRequestCode, Activity.RESULT_CANCELED, activity.intent) }
        return dialog.create()
    }
}
