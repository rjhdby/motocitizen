package motocitizen.network

import motocitizen.user.User
import org.json.JSONObject

abstract class ApiRequestWithAuth(callback: (JSONObject) -> Unit, login: String = User.name, passHash: String = User.passHash) : ApiRequest(callback) {
    init {
        params.put("l", login)
        params.put("p", passHash)
    }
}