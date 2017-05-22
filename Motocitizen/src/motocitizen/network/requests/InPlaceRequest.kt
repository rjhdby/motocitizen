package motocitizen.network.requests

import motocitizen.network.Methods
import motocitizen.network.ApiRequest
import motocitizen.user.User

class InPlaceRequest(id: Int) : ApiRequest() {
    init {
        params.put("login", User.dirtyRead().name)
        params.put("id", id.toString())
        params.put("m", Methods.IN_PLACE.code)
        call()
    }
}