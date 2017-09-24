package motocitizen.datasources.network.requests

import motocitizen.datasources.network.ApiRequest
import motocitizen.datasources.network.ApiResponse
import motocitizen.datasources.network.Methods
import motocitizen.datasources.preferences.Preferences.Stored.HOURS_AGO
import motocitizen.user.User

class AccidentListRequest(callback: (ApiResponse) -> Unit) : ApiRequest(callback) {
    init {
        with(params) {
            if (User.name != "") {
                put("u", User.name)
            }
            put("a", HOURS_AGO.string())
            put("m", Methods.LIST.code)
        }
        call()
    }
}