package motocitizen.datasources.network.requests

import motocitizen.datasources.network.ApiRequestWithAuth
import motocitizen.datasources.network.Methods
import org.json.JSONObject

class ActivateAccident(accidentId: Int, callback: (JSONObject) -> Unit) : ApiRequestWithAuth(callback) {
    init {
        params.put("m", Methods.ACTIVATE_ACCIDENT.code)
        params.put("id", accidentId.toString())
        call()
    }
}