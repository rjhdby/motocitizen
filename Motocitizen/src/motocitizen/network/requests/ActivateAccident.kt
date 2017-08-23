package motocitizen.network.requests

import motocitizen.network.Methods
import motocitizen.network.ApiRequestWithAuth

class ActivateAccident(accidentId: Int, callback: RequestResultCallback? = null) : ApiRequestWithAuth(callback) {
    init {
        params.put("m", Methods.ACTIVATE_ACCIDENT.code)
        params.put("id", accidentId.toString())
        call()
    }
}