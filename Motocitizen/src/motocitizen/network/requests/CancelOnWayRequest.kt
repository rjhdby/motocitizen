package motocitizen.network.requests

import motocitizen.network.Methods
import motocitizen.network.RequestWithAuth

class CancelOnWayRequest(id: Int, callback: RequestResultCallback) : RequestWithAuth(callback) {
    init {
        params.put("id", id.toString())
        params.put("calledMethod", Methods.CANCEL_ON_WAY.code)
        call()
    }
}