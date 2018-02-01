package motocitizen.datasources.network

import motocitizen.datasources.preferences.Preferences
import motocitizen.user.Auth
import motocitizen.utils.md5

//todo VK
abstract class ApiRequestWithAuth(callback: (ApiResponse) -> Unit = {}) : ApiRequest(callback) {
    init {
        if (Auth.getType() == Auth.AuthType.FORUM) {
            params["l"] = Preferences.login
            params["p"] = Preferences.password.md5()
        } else {
            params["v"] = Preferences.vkToken
        }
    }
}