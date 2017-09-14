package motocitizen.datasources.network.requests

import motocitizen.datasources.network.ApiRequestWithAuth
import motocitizen.datasources.network.Methods
import org.json.JSONObject

class CancelOnWayRequest(id: Int, callback: (JSONObject) -> Unit) : ApiRequestWithAuth(callback) {
    init {
        params.put("id", id.toString())
        params.put("m", Methods.CANCEL_ON_WAY.code)
        call()
    }
}