package motocitizen.content.accident

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import motocitizen.dictionary.AccidentStatus
import motocitizen.dictionary.Medicine
import motocitizen.dictionary.Type
import motocitizen.rows.accidentList.OwnedEndedRow
import motocitizen.rows.accidentList.Row
import java.util.*

class OwnedEndedAccident(id: Int, type: Type, damage: Medicine, time: Date, address: String, coordinates: LatLng, owner: Int) : Accident(id, type, damage, time, address, coordinates, owner) {
    //class OwnedEndedAccident(id: Int, type: Type, damage: Medicine, time: Date, address: String, coordinates: LatLng, owner: Owner) : Accident(id, type, damage, time, address, coordinates, owner) {
    override val status: AccidentStatus = AccidentStatus.ENDED

    override fun makeListRow(context: Context): Row = OwnedEndedRow(context, this)
    override fun isEnded(): Boolean = true
}