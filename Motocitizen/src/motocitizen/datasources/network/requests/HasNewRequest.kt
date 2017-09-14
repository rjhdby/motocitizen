package motocitizen.datasources.network.requests

import motocitizen.datasources.network.ApiRequest
import motocitizen.datasources.network.Methods
import org.json.JSONObject

class HasNewRequest(time: Long, callback: (JSONObject) -> Unit) : ApiRequest(callback) {
    init {
        params.put("ut", time.toString())
        params.put("m", Methods.HAS_NEW.code)
        call()
    }
}