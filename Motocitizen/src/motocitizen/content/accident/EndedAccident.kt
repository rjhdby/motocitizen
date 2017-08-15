package motocitizen.content.accident

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import motocitizen.dictionary.AccidentStatus
import motocitizen.dictionary.Medicine
import motocitizen.dictionary.Type
import motocitizen.rows.accidentList.EndedRow
import motocitizen.rows.accidentList.Row
import java.util.*

class EndedAccident(id: Int, type: Type, damage: Medicine, time: Date, address: String, coordinates: LatLng, owner: Int) : Accident(id, type, damage, time, address, coordinates, owner) {
    //class EndedAccident(id: Int, type: Type, damage: Medicine, time: Date, address: String, coordinates: LatLng, owner: OwnerLegacy) : Accident(id, type, damage, time, address, coordinates, owner) {
    override val status: AccidentStatus = AccidentStatus.ENDED

    override fun makeListRow(context: Context): Row = EndedRow(context, this)
    override fun isEnded(): Boolean = true
}