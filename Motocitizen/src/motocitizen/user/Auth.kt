package motocitizen.user

import com.vk.api.sdk.VK
import motocitizen.datasources.network.ApiResponse
import motocitizen.datasources.network.requests.AuthRequest
import motocitizen.datasources.preferences.Preferences
import motocitizen.utils.getEnumOr
import motocitizen.utils.tryOrPrintStack

object Auth {
    enum class AuthType(val value: String) {
        NONE("none"),
        ANON("anon"),
        FORUM("forum"),
        VK("vk"),
        GOOGLE("google");

        companion object {
            fun current(): AuthType = values().firstOrNull { it.value == Preferences.authType } ?: NONE
        }
    }

    fun auth(type: AuthType, callback: () -> Unit = {}) {
        Preferences.authType = type.value
        when (type) {
            AuthType.NONE                                -> {
                logout()
                callback()
            }
            AuthType.ANON                                -> {
                authAsAnon()
                callback()
            }
            AuthType.GOOGLE, AuthType.FORUM, AuthType.VK -> AuthRequest { authRequestCallback(it, callback) }.call()
        }
    }

    fun autoAuth(callback: () -> Unit) = auth(AuthType.current(), callback)

    private fun authRequestCallback(response: ApiResponse, callback: () -> Unit) {
        parseAuthResult(response)
        if (!User.isAuthorized) logout()
        callback()
    }

    fun isForumAuth() = AuthType.current() == AuthType.FORUM

    fun isGoogleAuth() = AuthType.current() == AuthType.GOOGLE

    private fun parseAuthResult(response: ApiResponse) {
        User.isAuthorized = false
        tryOrPrintStack {
            val result = response.resultObject
            User.authenticate(id = Integer.parseInt(result.getString("id")),
                              name = result.getString("l"),
                              role = result.getEnumOr("r", Role.RO))
            Preferences.login = User.name
        }
    }

    private fun authAsAnon() {
        logout()
        Preferences.authType = AuthType.ANON.value
        User.isAuthorized = true
    }

    fun logout() {
        VK.logout()
        Preferences.run {
            login = ""
            password = ""
            authType = AuthType.NONE.value
        }
        User.logout()
    }
}