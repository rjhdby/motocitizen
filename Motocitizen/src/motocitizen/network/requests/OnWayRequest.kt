package motocitizen.network.requests

import motocitizen.network.Methods
import motocitizen.network.NewApiRequestWithAuth

class OnWayRequest(id: Int, callback: RequestResultCallback) : NewApiRequestWithAuth(callback) {
    init {
        params.put("id", id.toString())
        params.put("m", Methods.ON_WAY.code)
        call()
    }
}