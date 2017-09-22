package motocitizen.datasources.network

import motocitizen.user.Auth
import motocitizen.user.User

abstract class ApiRequestWithAuth(callback: (ApiResponse) -> Unit = {},
                                  login: String = User.name,
                                  passHash: String = Auth.getPassHash()) : ApiRequest(callback) {
    init {
        with(params) {
            put("l", login)
            put("p", passHash)
        }
    }
}