package motocitizen.network.requests

import motocitizen.network.ApiRequestWithAuth
import motocitizen.network.Methods
import org.json.JSONObject

class SendMessageRequest(text: String, accidentId: Int, callback: (JSONObject) -> Unit) : ApiRequestWithAuth(callback) {
    init {
        params.put("m", Methods.MESSAGE.code)
        params.put("id", accidentId.toString())
        params.put("t", text)
        call()
    }
}