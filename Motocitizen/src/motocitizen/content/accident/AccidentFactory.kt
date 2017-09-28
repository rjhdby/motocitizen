package motocitizen.content.accident

import com.google.android.gms.maps.model.LatLng
import motocitizen.dictionary.AccidentStatus
import motocitizen.dictionary.Medicine
import motocitizen.dictionary.Type
import motocitizen.geo.geocoder.AccidentLocation
import motocitizen.utils.DateFromSeconds
import org.json.JSONObject

object AccidentFactory {
    fun make(json: JSONObject): Accident = AccidentBuilder()
            .id(json.getInt("id"))
            .status(AccidentStatus.parse(json.getString("s")))
            .type(Type.parse(json.getString("t")))
            .medicine(Medicine.parse(json.getString("m")))
            .time(DateFromSeconds(json.getLong("ut")))
            .location(makeLocation(json))
            .owner(json.getInt("o"))
            .description(json.getString("d"))
            .messagesCount(json.getInt("mc"))
            .build()

    private fun makeLocation(json: JSONObject) = AccidentLocation(json.getString("a"), LatLng(json.getDouble("y"), json.getDouble("x")))

    fun refactor(accident: Accident, status: AccidentStatus): Accident = AccidentBuilder()
            .from(accident)
            .status(status)
            .build()
}