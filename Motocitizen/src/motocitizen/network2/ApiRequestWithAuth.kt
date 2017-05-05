package motocitizen.network2

import motocitizen.user.User

abstract class ApiRequestWithAuth(params: HashMap<String, String>, callback: RequestResultCallback? = null) : ApiRequest(params, callback) {
    init {
        params.put("l", User.dirtyRead().name)
        params.put("p", User.dirtyRead().passHash)
    }
}