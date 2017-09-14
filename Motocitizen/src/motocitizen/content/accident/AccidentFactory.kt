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
                .id(json.getInt("id"))
                .status(AccidentStatus.parse(json.getString("s")))
                .type(Type.parse(json.getString("t")))
                .medicine(Medicine.parse(json.getString("m")))
                .time(Date(json.getLong("ut") * 1000))
                .address(json.getString("a"))
                .coordinates(LatLng(json.getDouble("y"), json.getDouble("x")))
                .owner(json.getInt("o"))
                .description(json.getString("d"))
                .messagesCount(json.getInt("mc"))
                .build()
    }

    fun refactor(accident: Accident, status: AccidentStatus): Accident {
        return AccidentBuilder()
                .from(accident)
                .status(status)
                .build()
    }
}