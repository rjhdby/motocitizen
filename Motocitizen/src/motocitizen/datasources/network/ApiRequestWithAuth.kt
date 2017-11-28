package motocitizen.datasources.network

import motocitizen.user.Auth
import motocitizen.user.User

abstract class ApiRequestWithAuth(login: String = User.name,
                                  passHash: String = Auth.getPassHash(),
                                  callback: (ApiResponse) -> Unit = {}) : ApiRequest(callback) {
    init {
        with(params) {
            put("l", login)
            put("p", passHash)
        }
    }
}