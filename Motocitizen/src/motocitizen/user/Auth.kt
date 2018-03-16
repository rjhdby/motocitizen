package motocitizen.user

import com.vk.sdk.VKSdk
import motocitizen.datasources.network.ApiResponse
import motocitizen.datasources.network.requests.AuthRequest
import motocitizen.datasources.preferences.Preferences
import org.json.JSONException

object Auth {
    enum class AuthType(val value: String) {
        NONE("none"),
        ANON("anon"),
        FORUM("forum"),
        VK("vk");

        companion object {
            fun current(): AuthType = values().firstOrNull { it.value == Preferences.authType } ?: NONE
        }
    }

    fun auth(type: AuthType, callback: () -> Unit = {}) {
        Preferences.authType = type.value
        when (type) {
            AuthType.NONE               -> {
                logoff()
                callback()
            }
            AuthType.ANON               -> {
                authAsAnon()
                callback()
            }
            AuthType.FORUM, AuthType.VK -> AuthRequest { authRequestCallback(it, callback) }.call()
        }
    }

    fun autoAuth(callback: () -> Unit) {
        auth(AuthType.current(), callback)
    }

    private fun authRequestCallback(response: ApiResponse, callback: () -> Unit) {
        parseAuthResult(response)
        if (!User.isAuthorized) logoff()
        callback()
    }

    fun getType(): AuthType = AuthType.current()

    private fun parseAuthResult(response: ApiResponse) {
        User.isAuthorized = false
        try {
            val result = response.resultObject
            User.apply {
                id = Integer.parseInt(result.getString("id"))
                name = result.getString("l")
                role = Role.parse(result.getInt("r"))
                isAuthorized = true
            }
            Preferences.login = User.name
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun authAsAnon() {
        logoff()
        Preferences.authType = AuthType.ANON.value
        User.isAuthorized = true
    }

    fun logoff() {
        VKSdk.logout()
        Preferences.apply {
            login = ""
            password = ""
//            vkToken = ""
            authType = AuthType.NONE.value
        }
        User.apply {
            name = ""
            role = Role.RO
            id = 0
            isAuthorized = false
        }
    }
}