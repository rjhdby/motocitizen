package motocitizen.network

import motocitizen.user.User

abstract class NewApiRequestWithAuth(callback: RequestResultCallback? = null, login: String = User.dirtyRead().name, passHash: String = User.dirtyRead().passHash) : ApiRequest(callback) {
    init {
        params.put("l", login)
        params.put("p", passHash)
    }
}