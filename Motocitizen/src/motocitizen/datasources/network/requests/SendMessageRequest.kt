package motocitizen.datasources.network.requests

import motocitizen.datasources.network.ApiRequestWithAuth
import motocitizen.datasources.network.LegacyApiResponse
import motocitizen.datasources.network.Methods

class SendMessageRequest(text: String, id: Int, callback: (LegacyApiResponse) -> Unit)
    : ApiRequestWithAuth(Methods.MESSAGE, id, callback = callback) {
    init {
        addParams("t" to text)
    }
}