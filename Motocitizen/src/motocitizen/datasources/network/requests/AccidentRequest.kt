package motocitizen.datasources.network.requests

import motocitizen.datasources.network.ApiRequest
import motocitizen.datasources.network.ApiResponse
import motocitizen.datasources.network.Methods
import motocitizen.datasources.preferences.Preferences

class AccidentRequest(id: Int, callback: (ApiResponse) -> Unit) : ApiRequest(Methods.ACCIDENT, id, callback = callback) {
    init {
        if (Preferences.isTester) addParams("test" to "1")
    }
}