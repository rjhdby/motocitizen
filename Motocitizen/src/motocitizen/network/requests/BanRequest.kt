package motocitizen.network.requests

import motocitizen.network.Methods
import motocitizen.network.NewApiRequestWithAuth

class BanRequest(id: Int, callback: RequestResultCallback) : NewApiRequestWithAuth(callback) {
    init {
        params.put("m", Methods.BAN.code)
        params.put("id", id.toString())
        call()
    }
}