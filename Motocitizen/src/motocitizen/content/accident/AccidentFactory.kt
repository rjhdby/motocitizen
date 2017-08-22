package motocitizen.content.accident

import com.google.android.gms.maps.model.LatLng
import motocitizen.dictionary.AccidentStatus
import motocitizen.dictionary.Medicine
import motocitizen.dictionary.Type
import org.json.JSONObject
import java.util.*

object AccidentFactory {
    fun make(json: JSONObject): Accident {
        return AccidentBuilder()
                .setId(json.getInt("id"))
                .setStatus(AccidentStatus.parse(json.getString("s")))
                .setType(Type.parse(json.getString("t")))
                .setMedicine(Medicine.parse(json.getString("m")))
                .setTime(Date(json.getLong("ut") * 1000))
                .setAddress(json.getString("a"))
                .setCoordinates(LatLng(json.getDouble("y"), json.getDouble("x")))
                .setOwner(json.getInt("o"))
                .setDescription(json.getString("d"))
                .build()
    }

    fun refactor(accident: Accident, status: AccidentStatus): Accident {
        return AccidentBuilder()
                .from(accident)
                .setStatus(status)
                .build()
    }
}