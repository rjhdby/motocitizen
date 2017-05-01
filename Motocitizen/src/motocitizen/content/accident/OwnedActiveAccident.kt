package motocitizen.content.accident

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import motocitizen.dictionary.AccidentStatus
import motocitizen.dictionary.Medicine
import motocitizen.dictionary.Type
import motocitizen.rows.accidentList.OwneActivedRow
import motocitizen.rows.accidentList.Row
import motocitizen.user.Owner
import java.util.*

class OwnedActiveAccident(id: Int, type: Type, damage: Medicine, time: Date, address: String, coordinates: LatLng, owner: Owner) : Accident(id, type, damage, time, address, coordinates, owner) {
    override val status: AccidentStatus = AccidentStatus.ACTIVE
    override fun makeListRow(context: Context): Row {
        return OwneActivedRow(context, this)
    }

    override fun isActive(): Boolean {
        return true
    }
}