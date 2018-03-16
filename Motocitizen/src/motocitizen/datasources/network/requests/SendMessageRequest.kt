package motocitizen.datasources.network.requests

import motocitizen.datasources.network.ApiRequestWithAuth
import motocitizen.datasources.network.ApiResponse
import motocitizen.datasources.network.Methods

class SendMessageRequest(text: String, id: Int, callback: (ApiResponse) -> Unit)
    : ApiRequestWithAuth(Methods.MESSAGE, id, callback = callback) {
    init {
        addParams("t" to text)
    }
}