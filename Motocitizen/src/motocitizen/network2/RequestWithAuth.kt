package motocitizen.network2

import motocitizen.user.User

abstract class RequestWithAuth(callback: RequestResultCallback? = null) : ApiRequest(callback) {
    init {
        params.put("login", User.dirtyRead().name)
        params.put("passhash", User.dirtyRead().passHash)
    }
}