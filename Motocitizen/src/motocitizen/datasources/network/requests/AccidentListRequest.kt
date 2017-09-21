package motocitizen.datasources.network.requests

import motocitizen.datasources.network.ApiRequest
import motocitizen.datasources.network.ApiResponse
import motocitizen.datasources.network.Methods
import motocitizen.datasources.preferences.Preferences
import motocitizen.user.User

class AccidentListRequest(callback: (ApiResponse) -> Unit) : ApiRequest(callback) {
    init {
        if (User.name != "") {
            params.put("u", User.name)
        }
        params.put("a", Preferences.hoursAgo.toString())
        params.put("m", Methods.LIST.code)
        call()
    }
}