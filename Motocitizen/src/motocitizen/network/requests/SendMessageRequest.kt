package motocitizen.network.requests

import motocitizen.network.Methods
import motocitizen.network.NewApiRequest
import motocitizen.user.User

class SendMessageRequest(text: String, accidentId: Int, callback: RequestResultCallback) : NewApiRequest(callback) {
    init {
        params.put("l", User.dirtyRead().name)
        params.put("p", User.dirtyRead().passHash)
        params.put("m", Methods.MESSAGE.code)
        params.put("id", accidentId.toString())
        params.put("t", text)
        call()
    }
}