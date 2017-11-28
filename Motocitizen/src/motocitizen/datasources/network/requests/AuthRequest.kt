package motocitizen.datasources.network.requests

import motocitizen.datasources.network.ApiRequestWithAuth
import motocitizen.datasources.network.ApiResponse
import motocitizen.datasources.network.Methods
import motocitizen.user.User

class AuthRequest(login: String, passHash: String, callback: (ApiResponse) -> Unit) : ApiRequestWithAuth(login, passHash, callback) {
    init {
        with(params) {
            put("m", Methods.AUTH.code)
            put("versionName", User.name)
        }
        call()
    }
}