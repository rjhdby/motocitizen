package motocitizen.network2

import motocitizen.user.User

abstract class RequestWithAuth(params: HashMap<String, String>, callback: RequestResultCallback? = null) : ApiRequest(params, callback) {
    init {
        params.put("login", User.dirtyRead().name)
        params.put("passwordHash", User.dirtyRead().passHash)
    }
}