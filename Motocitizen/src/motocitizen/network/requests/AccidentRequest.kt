package motocitizen.network.requests

import motocitizen.network.Methods
import motocitizen.network.NewApiRequest

class AccidentRequest(id: Int, callback: RequestResultCallback) : NewApiRequest(callback) {
    init {
        params.put("m", Methods.LIST.code)
        params.put("id", id.toString())
        call()
    }
}