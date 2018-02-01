package motocitizen.datasources.network.requests

import motocitizen.datasources.network.ApiRequest
import motocitizen.datasources.network.ApiResponse
import motocitizen.datasources.network.Methods

class DetailsRequest(id: Int, callback: (ApiResponse) -> Unit) : ApiRequest(callback) {
    override val method: String = Methods.DETAILS

    init {
        params["id"] = id.toString()
        call()
    }
}