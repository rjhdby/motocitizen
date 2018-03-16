package motocitizen.datasources.network.requests

import motocitizen.datasources.network.ApiRequest
import motocitizen.datasources.network.ApiResponse
import motocitizen.datasources.network.Methods

class HasNewRequest(time: Long, callback: (ApiResponse) -> Unit) : ApiRequest(Methods.HAS_NEW, callback = callback) {
    init {
        addParams("ut" to time.toString())
    }
}