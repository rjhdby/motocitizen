package motocitizen.network.requests

import motocitizen.network.Methods
import motocitizen.network.NewApiRequest

class DetailsRequest(id: Int, callback: RequestResultCallback) : NewApiRequest(callback) {
    init {
        params.put("id", id.toString())
        params.put("m", Methods.DETAILS.code)
        call()
    }
}