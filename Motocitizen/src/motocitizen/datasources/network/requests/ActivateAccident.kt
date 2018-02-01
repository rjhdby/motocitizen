package motocitizen.datasources.network.requests

import motocitizen.datasources.network.ApiRequestWithAuth
import motocitizen.datasources.network.ApiResponse
import motocitizen.datasources.network.Methods

class ActivateAccident(accidentId: Int, callback: (ApiResponse) -> Unit = {}) : ApiRequestWithAuth(callback = callback) {
    override val method: String = Methods.ACTIVATE_ACCIDENT

    init {
        params["id"] = accidentId.toString()
        call()
    }
}