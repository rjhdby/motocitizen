package motocitizen.datasources.network.requests

import motocitizen.datasources.network.ApiRequestWithAuth
import motocitizen.datasources.network.ApiResponse
import motocitizen.datasources.network.Methods

class BanRequest(id: Int, callback: (ApiResponse) -> Unit) : ApiRequestWithAuth(callback) {
    init {
        params.put("m", Methods.BAN.code)
        params.put("id", id.toString())
        call()
    }
}