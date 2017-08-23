package motocitizen.network.requests

import motocitizen.network.ApiRequest
import motocitizen.network.Methods
import motocitizen.user.User
import motocitizen.utils.Preferences

class AccidentListRequest(callback: RequestResultCallback) : ApiRequest(callback) {
    init {
        if (User.dirtyRead().name != "") {
            params.put("u", User.dirtyRead().name)
        }
        params.put("a", Preferences.hoursAgo.toString())
        params.put("m", Methods.LIST.code)
        call()
    }
}