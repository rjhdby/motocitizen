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
            private fun typeByValue(value: String): AuthType = try {
                values().filter { it.value == value }[0]
            } catch (e: Exception) {
                NONE
            }

            fun current(): AuthType = typeByValue(Preferences.authType)
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
            AuthType.FORUM, AuthType.VK -> AuthRequest { authRequestCallback(it, "", callback) }
        }
    }

    fun autoAuth(callback: () -> Unit) {
        auth(AuthType.current(), callback)
    }

    private fun authRequestCallback(response: ApiResponse, login: String, callback: () -> Unit) {
        parseAuthResult(response, login)
        if (!User.isAuthorized) logoff()
        callback()
    }

    fun getType(): AuthType = AuthType.current()

    private fun parseAuthResult(response: ApiResponse, login: String = "") {
        User.isAuthorized = false
        try {
            val result = response.resultObject
            User.apply {
                id = Integer.parseInt(result.getString("id"))
                name = if (login == "") result.getString("l") else login
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