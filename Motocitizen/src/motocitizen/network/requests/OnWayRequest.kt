package motocitizen.network.requests

import motocitizen.network.Methods
import motocitizen.network.ApiRequestWithAuth

class OnWayRequest(id: Int, callback: RequestResultCallback) : ApiRequestWithAuth(callback) {
    init {
        params.put("id", id.toString())
        params.put("m", Methods.ON_WAY.code)
        call()
    }
}