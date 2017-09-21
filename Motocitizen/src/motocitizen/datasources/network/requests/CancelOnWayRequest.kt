package motocitizen.datasources.network.requests

import motocitizen.datasources.network.ApiRequestWithAuth
import motocitizen.datasources.network.ApiResponse
import motocitizen.datasources.network.Methods

class CancelOnWayRequest(id: Int, callback: (ApiResponse) -> Unit) : ApiRequestWithAuth(callback) {
    init {
        params.put("id", id.toString())
        params.put("m", Methods.CANCEL_ON_WAY.code)
        call()
    }
}