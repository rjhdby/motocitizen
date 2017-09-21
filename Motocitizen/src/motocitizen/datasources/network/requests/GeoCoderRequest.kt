package motocitizen.datasources.network.requests

import motocitizen.datasources.network.ApiResponse
import motocitizen.datasources.network.CoreRequest
import org.json.JSONException
import org.json.JSONObject

class GeoCoderRequest(address: String, callback: (ApiResponse) -> Unit) : CoreRequest(callback) {
    override val url: String = "https://maps.googleapis.com/maps/api/geocode/json"

    init {
        params.put("language", "ru")
        params.put("address", address)
        call()
    }

    override fun response(string: String): ApiResponse {
        val parsed = try {
            val location = JSONObject(string).getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location")
            val lat = location.getDouble("lat")
            val lng = location.getDouble("lng")
            JSONObject(String.format("""{"r":{"lat":$lat,"lng":$lng},"e":{}}"""))
        } catch (e: JSONException) {
            error
        }
        return ApiResponse(parsed)
    }
}
