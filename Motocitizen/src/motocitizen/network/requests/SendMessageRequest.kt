package motocitizen.network.requests

import motocitizen.network.Methods
import motocitizen.network.ApiRequestWithAuth

class SendMessageRequest(text: String, accidentId: Int, callback: RequestResultCallback) : ApiRequestWithAuth(callback) {
    init {
        params.put("m", Methods.MESSAGE.code)
        params.put("id", accidentId.toString())
        params.put("t", text)
        call()
    }
}