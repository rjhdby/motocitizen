package motocitizen.datasources.network.requests

import motocitizen.datasources.network.ApiRequestWithAuth
import motocitizen.datasources.network.ApiResponse
import motocitizen.datasources.network.Methods

class OnWayRequest(id: Int, callback: (ApiResponse) -> Unit) : ApiRequestWithAuth(id, callback = callback) {
    override val method = Methods.ON_WAY
}