package motocitizen.network.requests

import motocitizen.network.ApiRequestWithAuth
import motocitizen.network.Methods
import org.json.JSONObject

class LeaveRequest(id: Int, callback: (JSONObject) -> Unit) : ApiRequestWithAuth(callback) {
    init {
        params.put("id", id.toString())
        params.put("m", Methods.LEAVE.code)
        call()
    }
}