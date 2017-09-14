package motocitizen.network.requests

import motocitizen.network.ApiRequestWithAuth
import motocitizen.network.Methods
import org.json.JSONObject

class BanRequest(id: Int, callback: (JSONObject) -> Unit) : ApiRequestWithAuth(callback) {
    init {
        params.put("m", Methods.BAN.code)
        params.put("id", id.toString())
        call()
    }
}