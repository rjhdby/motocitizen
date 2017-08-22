package motocitizen.network.requests

import motocitizen.network.Methods
import motocitizen.network.NewApiRequestWithAuth

class ActivateAccident(accidentId: Int, callback: RequestResultCallback? = null) : NewApiRequestWithAuth(callback) {
    init {
        params.put("m", Methods.ACTIVATE_ACCIDENT.code)
        params.put("id", accidentId.toString())
        call()
    }
}