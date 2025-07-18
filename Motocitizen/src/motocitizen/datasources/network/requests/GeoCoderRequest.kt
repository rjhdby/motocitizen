package motocitizen.datasources.network.requests

import motocitizen.datasources.network.LegacyApiResponse
import motocitizen.datasources.network.CoreRequest
import org.json.JSONException
import org.json.JSONObject

class GeoCoderRequest(address: String, callback: (LegacyApiResponse) -> Unit) : CoreRequest(callback) {
    override val url: String = "https://maps.googleapis.com/maps/api/geocode/json"

    init {
        addParams("language" to "ru", "address" to address)
    }

    override fun response(string: String): LegacyApiResponse {
        val parsed = try {
            val location = JSONObject(string).getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location")
            val lat = location.getDouble("lat")
            val lng = location.getDouble("lng")
            JSONObject(String.format("""{"r":{"lat":$lat,"lng":$lng},"e":{}}"""))
        } catch (_: JSONException) {
            error
        }
        return LegacyApiResponse(parsed)
    }
}
