package motocitizen.datasources.network.requests

import motocitizen.datasources.network.ApiRequestWithAuth
import motocitizen.datasources.network.ApiResponse
import motocitizen.datasources.network.Methods

class CancelOnWayRequest(id: Int, callback: (ApiResponse) -> Unit) : ApiRequestWithAuth(callback = callback) {
    override val method: String = Methods.CANCEL_ON_WAY

    init {
        params["id"] = id.toString()
        call()
    }
}