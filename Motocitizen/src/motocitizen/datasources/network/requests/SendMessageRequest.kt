package motocitizen.datasources.network.requests

import motocitizen.datasources.network.ApiRequestWithAuth
import motocitizen.datasources.network.ApiResponse
import motocitizen.datasources.network.Methods

class SendMessageRequest(text: String, accidentId: Int, callback: (ApiResponse) -> Unit) : ApiRequestWithAuth(callback = callback) {
    override val method = Methods.MESSAGE

    init {
        params["id"] = accidentId.toString()
        params["t"] = text
        call()
    }
}