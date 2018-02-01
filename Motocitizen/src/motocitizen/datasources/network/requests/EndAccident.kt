package motocitizen.datasources.network.requests

import motocitizen.datasources.network.ApiRequestWithAuth
import motocitizen.datasources.network.ApiResponse
import motocitizen.datasources.network.Methods

class EndAccident(accidentId: Int, callback: (ApiResponse) -> Unit = {}) : ApiRequestWithAuth(callback = callback) {
    override val method = Methods.END_ACCIDENT

    init {
        params["id"] = accidentId.toString()
        call()
    }
}