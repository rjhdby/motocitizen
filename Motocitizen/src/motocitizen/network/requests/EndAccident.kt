package motocitizen.network.requests

import motocitizen.network.Methods
import motocitizen.network.NewApiRequestWithAuth

class EndAccident(accidentId: Int, callback: RequestResultCallback? = null) : NewApiRequestWithAuth(callback) {
    init {
        params.put("m", Methods.END_ACCIDENT.code)
        params.put("id", accidentId.toString())
        call()
    }
}