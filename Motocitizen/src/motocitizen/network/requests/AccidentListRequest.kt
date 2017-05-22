package motocitizen.network.requests

import motocitizen.network.Methods
import motocitizen.network.ApiRequest
import motocitizen.user.User
import motocitizen.utils.Preferences

class AccidentListRequest(callback: RequestResultCallback) : ApiRequest(callback) {
    init {
        if (User.dirtyRead().name != "") {
            params.put("user", User.dirtyRead().name)
        }
        params.put("age", (if (Preferences.dirtyRead() == null) 24 else Preferences.dirtyRead()!!.hoursAgo).toString())
        params.put("m", Methods.GET_LIST.code)
        call()
    }
}