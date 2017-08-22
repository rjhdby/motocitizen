package motocitizen.network.requests

import motocitizen.network.Methods
import motocitizen.network.NewApiRequestWithAuth

class HideAccident(accidentId: Int, callback: RequestResultCallback? = null) : NewApiRequestWithAuth(callback) {
    init {
        params.put("m", Methods.HIDE_ACCIDENT.code)
        params.put("id", accidentId.toString())
        call()
    }
}