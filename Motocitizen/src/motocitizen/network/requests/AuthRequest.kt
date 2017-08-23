package motocitizen.network.requests

import motocitizen.network.Methods
import motocitizen.network.ApiRequestWithAuth
import motocitizen.user.User

class AuthRequest(login: String, passHash: String, callback: RequestResultCallback) : ApiRequestWithAuth(callback, login, passHash) {
    init {
        params.put("m", Methods.AUTH.code)
        params.put("versionName", User.dirtyRead().name.toString())
        call()
    }
}