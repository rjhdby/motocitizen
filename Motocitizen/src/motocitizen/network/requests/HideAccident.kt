package motocitizen.network.requests

import motocitizen.network.Methods
import motocitizen.network.ApiRequestWithAuth

class HideAccident(accidentId: Int, callback: RequestResultCallback? = null) : ApiRequestWithAuth(callback) {
    init {
        params.put("m", Methods.HIDE_ACCIDENT.code)
        params.put("id", accidentId.toString())
        call()
    }
}