package motocitizen.content.accident

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import motocitizen.dictionary.AccidentStatus
import motocitizen.dictionary.Medicine
import motocitizen.dictionary.Type
import motocitizen.rows.accidentList.HiddenRow
import motocitizen.rows.accidentList.Row
import motocitizen.user.Owner
import java.util.*

class HiddenAccident(id: Int, type: Type, damage: Medicine, time: Date, address: String, coordinates: LatLng, owner: Owner) : Accident(id, type, damage, time, address, coordinates, owner) {
    override val status: AccidentStatus = AccidentStatus.HIDDEN
    override fun makeListRow(context: Context): Row {
        return HiddenRow(context, this)
    }

    override fun isHidden(): Boolean {
        return false
    }
}