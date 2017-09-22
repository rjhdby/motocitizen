package motocitizen.datasources.network.requests

import motocitizen.datasources.network.ApiRequest
import motocitizen.datasources.network.ApiResponse
import motocitizen.datasources.network.Methods

class HasNewRequest(time: Long, callback: (ApiResponse) -> Unit) : ApiRequest(callback) {
    init {
        with(params) {
            put("ut", time.toString())
            put("m", Methods.HAS_NEW.code)
        }
        call()
    }
}