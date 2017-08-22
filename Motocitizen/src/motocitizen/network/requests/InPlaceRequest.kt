package motocitizen.network.requests

import motocitizen.network.Methods
import motocitizen.network.NewApiRequestWithAuth

class InPlaceRequest(id: Int, callback: RequestResultCallback) : NewApiRequestWithAuth(callback) {
    init {
        params.put("id", id.toString())
        params.put("m", Methods.IN_PLACE.code)
        call()
    }
}