package motocitizen.network.requests

import motocitizen.network.Methods
import motocitizen.network.ApiRequest
import motocitizen.user.User

class AuthRequest(login: String, passHash: String, callback: RequestResultCallback) : ApiRequest(callback) {
    init {
        params.put("calledMethod", Methods.AUTH.code)
        params.put("versionName", User.dirtyRead().name.toString())
        params.put("login", login)
        params.put("passwordHash", passHash)
        call()
    }
}