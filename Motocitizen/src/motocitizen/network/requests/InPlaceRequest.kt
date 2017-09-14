package motocitizen.network.requests

import motocitizen.network.ApiRequestWithAuth
import motocitizen.network.Methods
import org.json.JSONObject

class InPlaceRequest(id: Int, callback: (JSONObject) -> Unit) : ApiRequestWithAuth(callback) {
    init {
        params.put("id", id.toString())
        params.put("m", Methods.IN_PLACE.code)
        call()
    }
}