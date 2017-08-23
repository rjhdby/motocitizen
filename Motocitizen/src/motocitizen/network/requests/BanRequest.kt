package motocitizen.network.requests

import motocitizen.network.Methods
import motocitizen.network.ApiRequestWithAuth

class BanRequest(id: Int, callback: RequestResultCallback) : ApiRequestWithAuth(callback) {
    init {
        params.put("m", Methods.BAN.code)
        params.put("id", id.toString())
        call()
    }
}