package motocitizen.datasources.network

import motocitizen.user.Auth
import motocitizen.user.User
import org.json.JSONObject

abstract class ApiRequestWithAuth(callback: (JSONObject) -> Unit = {}, login: String = User.name, passHash: String = Auth.getPassHash()) : ApiRequest(callback) {
    init {
        params.put("l", login)
        params.put("p", passHash)
    }
}