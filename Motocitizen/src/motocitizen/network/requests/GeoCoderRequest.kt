package motocitizen.network.requests

import motocitizen.network.CoreRequest
import org.json.JSONObject

class GeoCoderRequest(address: String, callback: (JSONObject) -> Unit) : CoreRequest(callback) {
    override val url: String = "https://maps.googleapis.com/maps/api/geocode/json"

    init {
        params.put("language", "ru")
        params.put("address", address)
        call()
    }
}