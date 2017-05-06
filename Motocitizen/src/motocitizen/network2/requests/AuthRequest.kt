package motocitizen.network2.requests

import motocitizen.network.Methods
import motocitizen.network2.ApiRequest
import motocitizen.user.User

class AuthRequest(login: String, passHash: String, callback: RequestResultCallback) : ApiRequest(callback) {
    init {
        params.put("calledMethod", Methods.AUTH.toCode())
        params.put("versionName", User.dirtyRead().name.toString())
        params.put("login", login)
        params.put("passwordHash", passHash)
        call()
    }
}