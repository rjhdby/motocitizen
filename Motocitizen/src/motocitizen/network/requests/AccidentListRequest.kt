package motocitizen.network.requests

import motocitizen.network.ApiRequest
import motocitizen.network.Methods
import motocitizen.user.User
import motocitizen.utils.Preferences
import org.json.JSONObject

class AccidentListRequest(callback: (JSONObject) -> Unit) : ApiRequest(callback) {
    init {
        if (User.name != "") {
            params.put("u", User.name)
        }
        params.put("a", Preferences.hoursAgo.toString())
        params.put("m", Methods.LIST.code)
        call()
    }
}