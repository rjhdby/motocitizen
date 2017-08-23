package motocitizen.network.requests

import motocitizen.network.Methods
import motocitizen.network.ApiRequest

class DetailsRequest(id: Int, callback: RequestResultCallback) : ApiRequest(callback) {
    init {
        params.put("id", id.toString())
        params.put("m", Methods.DETAILS.code)
        call()
    }
}