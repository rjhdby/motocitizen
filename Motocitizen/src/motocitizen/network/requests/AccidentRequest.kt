package motocitizen.network.requests

import motocitizen.network.ApiRequest
import motocitizen.network.Methods

class AccidentRequest(id: Int, callback: RequestResultCallback) : ApiRequest(callback) {
    init {
        params.put("m", Methods.LIST.code)
        params.put("id", id.toString())
        call()
    }
}