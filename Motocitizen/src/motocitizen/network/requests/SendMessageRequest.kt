package motocitizen.network.requests

import motocitizen.network.Methods
import motocitizen.network.RequestWithAuth

class SendMessageRequest(text: String, accidentId: Int, callback: RequestResultCallback) : RequestWithAuth(callback) {
    init {
        params.put("calledMethod", Methods.MESSAGE.code)
        params.put("id", accidentId.toString())
        params.put("text", text)
        call()
    }
}