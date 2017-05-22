package motocitizen.network.requests

import motocitizen.network.Methods
import motocitizen.network.RequestWithAuth

class OnWayRequest(id: Int, callback: RequestResultCallback) : RequestWithAuth(callback) {
    init {
        params.put("id", id.toString())
        params.put("m", Methods.ON_WAY.code)
        call()
    }
}