package motocitizen.network.requests

import motocitizen.network.ApiRequestWithAuth
import motocitizen.network.Methods
import motocitizen.user.User

class AuthRequest(login: String, passHash: String, callback: RequestResultCallback) : ApiRequestWithAuth(callback, login, passHash) {
    init {
        params.put("m", Methods.AUTH.code)
        params.put("versionName", User.name)
        call()
    }
}