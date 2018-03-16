package motocitizen.datasources.network

import motocitizen.datasources.preferences.Preferences
import motocitizen.user.Auth
import motocitizen.utils.md5

//todo VK
abstract class ApiRequestWithAuth(method: String, id: Int? = null, callback: (ApiResponse) -> Unit = {}) : ApiRequest(method, id, callback) {
    override fun call() {
        if (Auth.getType() == Auth.AuthType.FORUM) {
            addParams(
                    "l" to Preferences.login,
                    "p" to Preferences.password.md5()
                     )
        } else {
            addParams("v" to Preferences.vkToken)
        }
        super.call()
    }
}