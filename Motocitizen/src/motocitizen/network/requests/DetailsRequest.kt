package motocitizen.network.requests

import motocitizen.network.ApiRequest
import motocitizen.network.Methods

class DetailsRequest(id: Int, callback: RequestResultCallback) : ApiRequest(callback) {
    init {
        params.put("id", id.toString())
        params.put("m", Methods.DETAILS.code)
        call()
    }
}