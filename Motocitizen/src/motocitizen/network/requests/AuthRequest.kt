package motocitizen.network.requests

import motocitizen.network.Methods
import motocitizen.network.NewApiRequest
import motocitizen.user.User

class AuthRequest(login: String, passHash: String, callback: RequestResultCallback) : NewApiRequest(callback) {
    init {
//        params.put("calledMethod", Methods.AUTH.code)
//        params.put("versionName", User.dirtyRead().name.toString())
//        params.put("login", login)
//        params.put("passwordHash", passHash)
        params.put("m", Methods.AUTH.code)
        params.put("versionName", User.dirtyRead().name.toString())
        params.put("l", login)
        params.put("p", passHash)
        call()
    }
}