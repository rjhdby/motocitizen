package motocitizen.datasources.network.requests

import motocitizen.datasources.network.ApiRequestWithAuth
import motocitizen.datasources.network.Methods
import org.json.JSONObject

class BanRequest(id: Int, callback: (JSONObject) -> Unit) : ApiRequestWithAuth(callback) {
    init {
        params.put("m", Methods.BAN.code)
        params.put("id", id.toString())
        call()
    }
}