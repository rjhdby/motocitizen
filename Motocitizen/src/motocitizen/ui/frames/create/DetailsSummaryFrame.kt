package motocitizen.ui.frames.create

import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import motocitizen.content.accident.Accident
import motocitizen.dictionary.AccidentStatus
import motocitizen.dictionary.Medicine
import motocitizen.main.R
import motocitizen.utils.distanceString
import motocitizen.utils.name
import motocitizen.utils.timeString

class DetailsSummaryFrame(val activity: FragmentActivity, val accident: Accident) {
    private val statusView = activity.findViewById(R.id.acc_details_general_status) as TextView
    private val medicineView = activity.findViewById(R.id.acc_details_medicine) as TextView
    private val typeView = activity.findViewById(R.id.acc_details_general_type) as TextView
    private val timeView = activity.findViewById(R.id.acc_details_general_time) as TextView
    private val ownerView = activity.findViewById(R.id.acc_details_general_owner) as TextView
    private val addressView = activity.findViewById(R.id.acc_details_general_address) as TextView
    private val distanceView = activity.findViewById(R.id.acc_details_general_distance) as TextView
    private val descriptionView = activity.findViewById(R.id.acc_details_general_description) as TextView

    fun update() {
//        with(activity) {
        val actionBar = (activity as AppCompatActivity).supportActionBar
        //TODO Разобраться с nullPointerException и убрать костыль
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = accident.type.text + ". " + accident.distanceString()

        statusView.visibility = if (accident.status == AccidentStatus.ACTIVE) View.GONE else View.VISIBLE
        medicineView.visibility = if (accident.medicine == Medicine.UNKNOWN) View.GONE else View.VISIBLE
        typeView.text = accident.type.text
        medicineView.text = "(${accident.medicine.text})"
        statusView.text = accident.status.text
        timeView.text = accident.time.timeString()
        ownerView.text = accident.owner.name()
        addressView.text = accident.address
        distanceView.text = accident.distanceString()
        descriptionView.text = accident.description
//        }
    }
}