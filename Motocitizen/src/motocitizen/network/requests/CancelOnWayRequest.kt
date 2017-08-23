package motocitizen.network.requests

import motocitizen.network.Methods
import motocitizen.network.ApiRequestWithAuth

class CancelOnWayRequest(id: Int, callback: RequestResultCallback) : ApiRequestWithAuth(callback) {
    init {
        params.put("id", id.toString())
        params.put("m", Methods.CANCEL_ON_WAY.code)
        call()
    }
}