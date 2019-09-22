package motocitizen.datasources.network

import motocitizen.datasources.preferences.Preferences
import motocitizen.user.Auth
import motocitizen.utils.md5

abstract class ApiRequestWithAuth(method: String, id: Int? = null, callback: (ApiResponse) -> Unit = {})
    : ApiRequest(method, id, callback) {
    override fun call() {
        when {
            Auth.isForumAuth()  -> addParams(
                    "l" to Preferences.login,
                    "p" to Preferences.password.md5()
                                            )
            Auth.isGoogleAuth() -> addParams(
                    "g" to Preferences.googleAccount,
                    "n" to Preferences.googleName
                                            )
            else                -> addParams(
                    "v" to Preferences.vkToken
                                            )
        }
        super.call()
    }
}