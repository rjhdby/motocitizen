package motocitizen.datasources.network.requests

import motocitizen.datasources.network.ApiRequestWithAuth
import motocitizen.datasources.network.ApiResponse
import motocitizen.datasources.network.Methods

class SendMessageRequest(text: String, accidentId: Int, callback: (ApiResponse) -> Unit) : ApiRequestWithAuth(callback = callback) {
    init {
        with(params) {
            put("m", Methods.MESSAGE.code)
            put("id", accidentId.toString())
            put("t", text)
        }
        call()
    }
}