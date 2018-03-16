package motocitizen.ui.fragments

import android.app.Activity
import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import motocitizen.content.Content
import motocitizen.content.accident.Accident
import motocitizen.datasources.network.requests.CancelOnWayRequest
import motocitizen.datasources.network.requests.OnWayRequest
import motocitizen.datasources.preferences.Preferences
import motocitizen.main.R
import motocitizen.ui.activity.AccidentDetailsActivity
import motocitizen.ui.activity.AccidentDetailsActivity.Companion.ACCIDENT_ID_KEY
import motocitizen.ui.dialogs.details.ConfirmDialog
import motocitizen.ui.rows.volunteer.VolunteerRowFactory
import motocitizen.utils.hide
import motocitizen.utils.isActive
import motocitizen.utils.show
import org.jetbrains.anko.runOnUiThread

class DetailVolunteersFragment() : Fragment() {
    companion object {
        private const val DIALOG_ON_WAY_CONFIRM = 1
        private const val DIALOG_CANCEL_ON_WAY_CONFIRM = 2
    }

    private lateinit var rootView: View
    private lateinit var confirmButton: ImageButton
    private lateinit var cancelButton: ImageButton
    private lateinit var disabledButton: ImageButton
    private lateinit var content: ViewGroup
    private lateinit var toMapButton: View

    private lateinit var accident: Accident

    constructor(accident: Accident) : this() {
        this.accident = accident
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_detail_volunteers, container, false)
        bindViews()
        disabledButton.isEnabled = false
        setUpListeners()
        update()
        return rootView
    }

    private fun bindViews() {
        confirmButton = rootView.findViewById(R.id.onway_button)
        cancelButton = rootView.findViewById(R.id.onway_cancel_button)
        disabledButton = rootView.findViewById(R.id.onway_disabled_button)
        content = rootView.findViewById(R.id.acc_onway_table)
        toMapButton = rootView.findViewById(R.id.details_to_map_button)
    }

    private fun setUpListeners() {
        confirmButton.setOnClickListener { showOnWayDialog() }
        cancelButton.setOnClickListener { showCancelDialog() }
        toMapButton.setOnClickListener { (activity as AccidentDetailsActivity).jumpToMap() }
    }

    private fun update() {
        setupAccess()
        content.removeAllViews()
        accident.volunteers.forEach { content.addView(VolunteerRowFactory.make(activity, it)) }
    }

    private fun setupAccess() =runOnUiThread{
        cancelButton.apply { if (canShowCancel()) show() else hide() }
        confirmButton.apply { if (canShowConfirm()) show() else hide() }
        disabledButton.apply { if (canShowDisabled()) show() else hide() }
    }

    private fun canShowCancel() = accident.isActive() && accident != Content.inPlace && accident.id == Preferences.onWay

    private fun canShowConfirm() = accident.isActive() && accident != Content.inPlace && accident.id != Preferences.onWay

    private fun canShowDisabled() = accident.isActive() && accident == Content.inPlace

    private fun showOnWayDialog() {
        val onWayConfirm = ConfirmDialog(activity.getString(R.string.title_dialog_onway_confirm))
        onWayConfirm.setTargetFragment(this, DIALOG_ON_WAY_CONFIRM)
        onWayConfirm.show(fragmentManager, "dialog")
    }

    private fun showCancelDialog() {
        val cancelOnWayConfirm = ConfirmDialog(activity.getString(R.string.title_dialog_cancel_onway_confirm))
        cancelOnWayConfirm.setTargetFragment(this, DIALOG_CANCEL_ON_WAY_CONFIRM)
        cancelOnWayConfirm.show(fragmentManager, "dialog")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (resultCode == Activity.RESULT_CANCELED) return
        when (requestCode) {
            DIALOG_ON_WAY_CONFIRM        -> sendOnWay()
            DIALOG_CANCEL_ON_WAY_CONFIRM -> sendCancelOnWay()
        }
    }

    private fun sendOnWay() {
        Preferences.onWay = accident.id
        OnWayRequest(accident.id, { setupAccess() }).call() //todo
    }

    private fun sendCancelOnWay() {
        Preferences.onWay = 0
        CancelOnWayRequest(accident.id, { setupAccess() }).call() //todo
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(ACCIDENT_ID_KEY, accident.id)
        super.onSaveInstanceState(outState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState == null) return
        accident = Content[savedInstanceState.getInt(ACCIDENT_ID_KEY)]!!
    }
}
