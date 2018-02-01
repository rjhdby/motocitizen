package motocitizen.datasources.network.requests

import motocitizen.datasources.network.ApiRequest
import motocitizen.datasources.network.ApiResponse
import motocitizen.datasources.network.Methods

class HasNewRequest(time: Long, callback: (ApiResponse) -> Unit) : ApiRequest(callback) {
    override val method = Methods.HAS_NEW

    init {
        params["ut"] = time.toString()
        call()
    }
}