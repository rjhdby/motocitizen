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
import motocitizen.ui.activity.AccidentDetailsActivity.ACCIDENT_ID_KEY
import motocitizen.ui.dialogs.details.ConfirmDialog
import motocitizen.ui.rows.volunteer.VolunteerRowFactory
import motocitizen.user.User
import motocitizen.utils.isActive

class DetailVolunteersFragment() : Fragment() {
    private val ROOT_LAYOUT = R.layout.fragment_detail_volunteers
    private val CONTENT_VIEW = R.id.acc_onway_table
    private val TO_MAP_BUTTON = R.id.details_to_map_button
    private val CONFIRM_BUTTON = R.id.onway_button
    private val CANCEL_BUTTON = R.id.onway_cancel_button
    private val DISABLED_BUTTON = R.id.onway_disabled_button

    private val DIALOG_ON_WAY_CONFIRM = 1
    private val DIALOG_CANCEL_ON_WAY_CONFIRM = 2

    private lateinit var confirmButton: ImageButton
    private lateinit var cancelButton: ImageButton
    private lateinit var disabledButton: ImageButton
    private lateinit var content: ViewGroup

    private lateinit var accident: Accident

    constructor(accident: Accident) : this() {
        this.accident = accident
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(ROOT_LAYOUT, container, false)

        val toMapButton = rootView.findViewById(TO_MAP_BUTTON)
        confirmButton = rootView.findViewById(CONFIRM_BUTTON) as ImageButton
        cancelButton = rootView.findViewById(CANCEL_BUTTON) as ImageButton
        disabledButton = rootView.findViewById(DISABLED_BUTTON) as ImageButton
        content = rootView.findViewById(CONTENT_VIEW) as ViewGroup

        disabledButton.isEnabled = false

        confirmButton.setOnClickListener { _ -> showOnWayDialog() }
        cancelButton.setOnClickListener { _ -> showCancelDialog() }
        toMapButton.setOnClickListener { _ -> (activity as AccidentDetailsActivity).jumpToMap() }
        update()
        return rootView
    }

    private fun update() {
        setupAccess()
        content.removeAllViews()
        accident.volunteers.forEach { action -> content.addView(VolunteerRowFactory.make(activity, action)) }
    }

    private fun setupAccess() {
        with(accident) {
            val active = accident.isActive() && User.isAuthorized
            confirmButton.visibility = if (id != Preferences.onWay && accident != Content.inPlace && active) View.VISIBLE else View.GONE
            cancelButton.visibility = if (id == Preferences.onWay && accident != Content.inPlace && active) View.VISIBLE else View.GONE
            disabledButton.visibility = if (accident == Content.inPlace && active) View.VISIBLE else View.GONE
        }
    }

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
        OnWayRequest(accident.id, { _ -> })//todo
    }

    private fun sendCancelOnWay() {
        Preferences.onWay = 0
        CancelOnWayRequest(accident.id, { _ -> }) //todo
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(ACCIDENT_ID_KEY, accident.id)
        super.onSaveInstanceState(outState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState == null) return
        accident = Content.accident(savedInstanceState.getInt(ACCIDENT_ID_KEY))
    }
}
