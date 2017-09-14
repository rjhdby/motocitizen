package motocitizen.datasources.network.requests

import motocitizen.datasources.network.ApiRequestWithAuth
import motocitizen.datasources.network.Methods
import org.json.JSONObject

class HideAccident(accidentId: Int, callback: (JSONObject) -> Unit) : ApiRequestWithAuth(callback) {
    init {
        params.put("m", Methods.HIDE_ACCIDENT.code)
        params.put("id", accidentId.toString())
        call()
    }
}