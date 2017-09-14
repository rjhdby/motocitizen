package motocitizen.network.requests

import motocitizen.network.ApiRequest
import motocitizen.network.Methods
import org.json.JSONObject

class AccidentRequest(id: Int, callback: (JSONObject) -> Unit) : ApiRequest(callback) {
    init {
        params.put("m", Methods.LIST.code)
        params.put("id", id.toString())
        call()
    }
}