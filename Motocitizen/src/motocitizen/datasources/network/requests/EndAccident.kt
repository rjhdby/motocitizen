package motocitizen.datasources.network.requests

import motocitizen.datasources.network.ApiRequestWithAuth
import motocitizen.datasources.network.ApiResponse
import motocitizen.datasources.network.Methods

class EndAccident(accidentId: Int, callback: (ApiResponse) -> Unit = {}) : ApiRequestWithAuth(callback = callback) {
    init {
        params.apply {
            put("m", Methods.END_ACCIDENT.code)
            put("id", accidentId.toString())
        }
        call()
    }
}