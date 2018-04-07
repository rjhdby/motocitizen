package motocitizen.utils

import com.google.android.gms.maps.model.LatLng
import motocitizen.dictionary.Dictionary
import motocitizen.geo.geocoder.AccidentLocation
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

fun JSONObject.getTimeFromSeconds() = Date(getLong("ut") * 1000)
fun JSONObject.getAccidentLocation() = AccidentLocation(getString("a"), LatLng(getDouble("y"), getDouble("x")))

inline fun <reified T : Enum<T>> JSONObject.getEnumOr(code: String, default: T): T = enumValues<T>().firstOrNull {
    val el = it as Dictionary<Any>
    el.code == when (el.code) {
        is Int -> getInt(code)
        else   -> getString(code)
    }
} ?: default

fun JSONArray.asList(): List<JSONObject> = (0 until length()).map { getJSONObject(it) }.toList()

fun JSONObject.getStringOr(key: String, default: String): String = getOr(key, default) { getString(key) }
fun JSONObject.getIntOr(key: String, default: Int): Int = getOr(key, default) { getInt(key) }
fun JSONObject.getArrayOrEmpty(key: String): JSONArray = getOr(key, JSONArray()) { getJSONArray(key) }
fun JSONObject.getObjectOrEmpty(key: String): JSONObject = getOr(key, JSONObject()) { getJSONObject(key) }

private fun <T> getOr(key: String, default: T, getter: (String) -> T?): T = try {
    getter(key) ?: default
} catch (e: JSONException) {
    default
}