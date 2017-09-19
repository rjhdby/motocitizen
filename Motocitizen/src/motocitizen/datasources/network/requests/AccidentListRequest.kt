package motocitizen.datasources.network.requests

import motocitizen.datasources.network.ApiRequest
import motocitizen.datasources.network.Methods
import motocitizen.user.User
import motocitizen.datasources.preferences.Preferences
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