package motocitizen.datasources.network.requests

import motocitizen.datasources.network.ApiRequest
import motocitizen.datasources.network.ApiResponse
import motocitizen.datasources.network.Methods

class DetailsRequest(id: Int, callback: (ApiResponse) -> Unit) : ApiRequest(callback) {
    init {
        params.put("id", id.toString())
        params.put("m", Methods.DETAILS.code)
        call()
    }
}