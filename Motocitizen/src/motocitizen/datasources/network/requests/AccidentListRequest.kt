package motocitizen.datasources.network.requests

import motocitizen.datasources.network.ApiRequest
import motocitizen.datasources.network.ApiResponse
import motocitizen.datasources.network.Methods
import motocitizen.datasources.preferences.Preferences
import motocitizen.user.User

class AccidentListRequest(callback: (ApiResponse) -> Unit) : ApiRequest(callback) {
    init {
        params.apply {
            if (User.name != "") {
                put("u", User.name)
            }
            put("a", Preferences.hoursAgo.toString())
            put("m", Methods.LIST.code)
        }
        call()
    }
}