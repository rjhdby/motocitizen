package motocitizen.datasources.network.requests

import motocitizen.datasources.network.ApiRequest
import motocitizen.datasources.network.Methods
import org.json.JSONObject

class AccidentRequest(id: Int, callback: (JSONObject) -> Unit) : ApiRequest(callback) {
    init {
        params.put("m", Methods.LIST.code)
        params.put("id", id.toString())
        call()
    }
}