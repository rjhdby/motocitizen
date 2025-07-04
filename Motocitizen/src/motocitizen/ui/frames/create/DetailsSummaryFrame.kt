package motocitizen.ui.frames.create

import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import motocitizen.content.accident.Accident
import motocitizen.dictionary.AccidentStatus
import motocitizen.dictionary.Medicine
import motocitizen.main.R
import motocitizen.utils.distanceString
import motocitizen.utils.name
import motocitizen.utils.timeString

class DetailsSummaryFrame(val activity: FragmentActivity, val accident: Accident) {
    private val statusView: TextView = activity.findViewById(R.id.acc_details_general_status)
    private val medicineView: TextView = activity.findViewById(R.id.acc_details_medicine)
    private val typeView: TextView = activity.findViewById(R.id.acc_details_general_type)
    private val timeView: TextView = activity.findViewById(R.id.acc_details_general_time)
    private val ownerView: TextView = activity.findViewById(R.id.acc_details_general_owner)
    private val addressView: TextView = activity.findViewById(R.id.acc_details_general_address)
    private val distanceView: TextView = activity.findViewById(R.id.acc_details_general_distance)
    private val descriptionView: TextView =
        activity.findViewById(R.id.acc_details_general_description)

    fun update() {
        val actionBar = (activity as AppCompatActivity).supportActionBar
        //TODO Разобраться с nullPointerException и убрать костыль
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = accident.type.text + ". " + accident.distanceString()

        statusView.visibility = if (accident.status == AccidentStatus.ACTIVE) View.GONE else View.VISIBLE
        medicineView.visibility = if (accident.medicine == Medicine.UNKNOWN) View.GONE else View.VISIBLE
        typeView.text = accident.type.text
        medicineView.text = buildString {
            append("(")
            append(accident.medicine.text)
            append(")")
        }
        statusView.text = accident.status.text
        timeView.text = accident.time.timeString()
        ownerView.text = accident.owner.name()
        addressView.text = accident.address
        distanceView.text = accident.distanceString()
        descriptionView.text = accident.description
    }
}