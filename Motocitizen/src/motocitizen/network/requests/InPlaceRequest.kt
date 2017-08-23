package motocitizen.network.requests

import motocitizen.network.Methods
import motocitizen.network.ApiRequestWithAuth

class InPlaceRequest(id: Int, callback: RequestResultCallback? = null) : ApiRequestWithAuth(callback) {
    init {
        params.put("id", id.toString())
        params.put("m", Methods.IN_PLACE.code)
        call()
    }
}