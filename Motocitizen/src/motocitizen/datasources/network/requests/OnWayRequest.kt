package motocitizen.datasources.network.requests

import motocitizen.datasources.network.ApiRequestWithAuth
import motocitizen.datasources.network.ApiResponse
import motocitizen.datasources.network.Methods

class OnWayRequest(id: Int, callback: (ApiResponse) -> Unit) : ApiRequestWithAuth(callback = callback) {
    init {
        with(params) {
            put("id", id.toString())
            put("m", Methods.ON_WAY.code)
        }
        call()
    }
}