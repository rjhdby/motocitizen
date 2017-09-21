package motocitizen.datasources.network.requests

import motocitizen.datasources.network.ApiRequestWithAuth
import motocitizen.datasources.network.ApiResponse
import motocitizen.datasources.network.Methods

class SendMessageRequest(text: String, accidentId: Int, callback: (ApiResponse) -> Unit) : ApiRequestWithAuth(callback) {
    init {
        params.put("m", Methods.MESSAGE.code)
        params.put("id", accidentId.toString())
        params.put("t", text)
        call()
    }
}