package motocitizen.utils

import com.google.android.gms.maps.model.LatLng
import motocitizen.dictionary.*
import motocitizen.geo.geocoder.AccidentLocation
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

fun JSONObject.getAccidentStatus() = AccidentStatus.values().firstOrNull { it.code == getString("s") } ?: AccidentStatus.ACTIVE
fun JSONObject.getAccidentType() = Type.values().firstOrNull { it.code == getString("t") } ?: Type.OTHER
fun JSONObject.getAccidentMedicine() = Medicine.values().firstOrNull { it.code == getString("m") } ?: Medicine.UNKNOWN
fun JSONObject.getTimeFromSeconds() = dateFromSeconds(getLong("ut"))
fun JSONObject.getAccidentLocation() = AccidentLocation(getString("a"), LatLng(getDouble("y"), getDouble("x")))
fun JSONObject.getHistoryAction() = HistoryAction.values().firstOrNull { it.code == getString("a") } ?: HistoryAction.OTHER
fun JSONObject.getVolunteerAction() = VolunteerActions.values().firstOrNull { it.code == getString("s") } ?: VolunteerActions.ON_WAY

fun JSONObject.getIntOr(key: String, default: Int): Int = try {
    getInt(key)
} catch (e: JSONException) {
    default
}

fun JSONObject.getStringOr(key: String, default: String): String = try {
    getString(key) ?: default
} catch (e: JSONException) {
    default
}

fun JSONObject.getArrayOrEmpty(key: String): JSONArray = try {
    getJSONArray(key)
} catch (e: JSONException) {
    JSONArray()
}

fun JSONObject.getObjectOrEmpty(key: String): JSONObject = try {
    getJSONObject(key)
} catch (e: JSONException) {
    JSONObject()
}