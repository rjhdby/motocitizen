package motocitizen.network.requests

import motocitizen.network.ApiRequestWithAuth
import motocitizen.network.Methods
import motocitizen.user.User
import org.json.JSONObject

class AuthRequest(login: String, passHash: String, callback: (JSONObject) -> Unit) : ApiRequestWithAuth(callback, login, passHash) {
    init {
        params.put("m", Methods.AUTH.code)
        params.put("versionName", User.name)
        call()
    }
}