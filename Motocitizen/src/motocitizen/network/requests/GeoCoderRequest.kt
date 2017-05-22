package motocitizen.network.requests

import motocitizen.network.CoreRequest

class GeoCoderRequest(address: String, callback: RequestResultCallback) : CoreRequest(callback) {
    override val url: String = "https://maps.googleapis.com/maps/api/geocode/json"

    init {
        params.put("language", "ru")
        params.put("address", address)
        call()
    }
}