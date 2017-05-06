package motocitizen.network2.requests

import motocitizen.network.Methods
import motocitizen.network2.ApiRequest
import motocitizen.user.User

class InPlaceRequest(id: Int) : ApiRequest() {
    init {
        params.put("login", User.dirtyRead().name)
        params.put("id", id.toString())
        params.put("m", Methods.IN_PLACE.toCode())
        call()
    }
}