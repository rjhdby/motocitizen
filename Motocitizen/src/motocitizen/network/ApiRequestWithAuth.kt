package motocitizen.network

import motocitizen.user.User

abstract class ApiRequestWithAuth(callback: RequestResultCallback? = null, login: String = User.name, passHash: String = User.passHash) : ApiRequest(callback) {
    init {
        params.put("l", login)
        params.put("p", passHash)
    }
}