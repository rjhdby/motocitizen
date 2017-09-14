package motocitizen.network.requests

import motocitizen.network.ApiRequest
import motocitizen.network.Methods
import org.json.JSONObject

class DetailsRequest(id: Int, callback: (JSONObject) -> Unit) : ApiRequest(callback) {
    init {
        params.put("id", id.toString())
        params.put("m", Methods.DETAILS.code)
        call()
    }
}