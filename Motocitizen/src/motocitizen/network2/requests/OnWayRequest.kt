package motocitizen.network2.requests

import motocitizen.network.Methods
import motocitizen.network2.RequestWithAuth

class OnWayRequest(id: Int, callback: RequestResultCallback) : RequestWithAuth(callback) {
    init {
        params.put("id", id.toString())
        params.put("m", Methods.ON_WAY.toCode())
        call()
    }
}