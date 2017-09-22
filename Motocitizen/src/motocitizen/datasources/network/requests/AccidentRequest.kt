package motocitizen.datasources.network.requests

import motocitizen.datasources.network.ApiRequest
import motocitizen.datasources.network.ApiResponse
import motocitizen.datasources.network.Methods

class AccidentRequest(id: Int, callback: (ApiResponse) -> Unit) : ApiRequest(callback) {
    init {
        with(params) {
            put("m", Methods.LIST.code)
            put("id", id.toString())
        }
        call()
    }
}