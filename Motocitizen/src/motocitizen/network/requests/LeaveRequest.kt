package motocitizen.network.requests

import motocitizen.network.Methods
import motocitizen.network.NewApiRequestWithAuth

class LeaveRequest(id: Int, callback: RequestResultCallback? = null) : NewApiRequestWithAuth(callback) {
    init {
        params.put("id", id.toString())
        params.put("m", Methods.LEAVE.code)
        call()
    }
}