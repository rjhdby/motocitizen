package motocitizen.network.requests

import motocitizen.network.Methods
import motocitizen.network.RequestWithAuth

class AccidentChangeStateRequest(state: String, accidentId: Int, callback: RequestResultCallback? = null) : RequestWithAuth(callback) {
    init {
        params.put("m", Methods.CHANGE_STATE.code)
        params.put("id", accidentId.toString())
        params.put("state", state)
        call()
    }
}