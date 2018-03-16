package motocitizen.datasources.network.requests

import motocitizen.datasources.network.ApiRequest
import motocitizen.datasources.network.ApiResponse
import motocitizen.datasources.network.Methods
import motocitizen.datasources.preferences.Preferences
import motocitizen.user.User

class AccidentListRequest(callback: (ApiResponse) -> Unit) : ApiRequest(Methods.LIST, callback = callback) {
    init {
        addParams("a" to Preferences.hoursAgo.toString())
        if (User.name != "") addParams("u" to User.name)
        if (Preferences.isTester) addParams("test" to "1")
    }
}