package motocitizen.datasources.network.requests

import motocitizen.datasources.network.ApiRequest
import motocitizen.datasources.network.ApiResponse
import motocitizen.datasources.network.Methods
import motocitizen.datasources.preferences.Preferences

class AccidentRequest(id: Int, callback: (ApiResponse) -> Unit) : ApiRequest(callback) {
    override val method: String = Methods.ACCIDENT

    init {
        params.apply {
            put("id", id.toString())
            if (Preferences.isTester) {
                put("test", "1")
            }
        }
        call()
    }
}