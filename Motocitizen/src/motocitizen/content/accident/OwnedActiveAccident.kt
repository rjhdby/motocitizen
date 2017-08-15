package motocitizen.content.accident

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import motocitizen.dictionary.AccidentStatus
import motocitizen.dictionary.Medicine
import motocitizen.dictionary.Type
import motocitizen.rows.accidentList.OwnedActiveRow
import motocitizen.rows.accidentList.Row
import java.util.*

class OwnedActiveAccident(id: Int, type: Type, damage: Medicine, time: Date, address: String, coordinates: LatLng, owner: Int) : Accident(id, type, damage, time, address, coordinates, owner) {
    //class OwnedActiveAccident(id: Int, type: Type, damage: Medicine, time: Date, address: String, coordinates: LatLng, owner: OwnerLegacy) : Accident(id, type, damage, time, address, coordinates, owner) {
    override val status: AccidentStatus = AccidentStatus.ACTIVE

    override fun makeListRow(context: Context): Row = OwnedActiveRow(context, this)
    override fun isActive(): Boolean = true
}