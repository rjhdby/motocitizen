package motocitizen.datasources.network.requests

import motocitizen.datasources.network.ApiRequestWithAuth
import motocitizen.datasources.network.ApiResponse
import motocitizen.datasources.network.Methods

class HideAccident(accidentId: Int, callback: (ApiResponse) -> Unit) : ApiRequestWithAuth(callback) {
    init {
        with(params) {
            put("m", Methods.HIDE_ACCIDENT.code)
            put("id", accidentId.toString())
        }
        call()
    }
}