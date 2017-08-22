package motocitizen.network.requests

import motocitizen.network.Methods
import motocitizen.network.NewApiRequestWithAuth

class CancelOnWayRequest(id: Int, callback: RequestResultCallback) : NewApiRequestWithAuth(callback) {
    init {
        params.put("id", id.toString())
        params.put("m", Methods.CANCEL_ON_WAY.code)
        call()
    }
}