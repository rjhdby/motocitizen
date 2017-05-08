package motocitizen.network2.requests

import motocitizen.network.Methods
import motocitizen.network2.ApiRequest
import motocitizen.user.User

class LeaveRequest(id: Int) : ApiRequest() {
    init {
        params.put("login", User.dirtyRead().name)
        params.put("id", id.toString())
        params.put("m", Methods.LEAVE.code)
        call()
    }
}