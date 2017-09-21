package motocitizen.datasources.network.requests

import motocitizen.datasources.network.ApiRequest
import motocitizen.datasources.network.ApiResponse
import motocitizen.datasources.network.Methods

class AccidentRequest(id: Int, callback: (ApiResponse) -> Unit) : ApiRequest(callback) {
    init {
        params.put("m", Methods.LIST.code)
        params.put("id", id.toString())
        call()
    }
}