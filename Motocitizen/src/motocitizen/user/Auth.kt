package motocitizen.user

import android.util.Log
import motocitizen.datasources.network.ApiResponse
import motocitizen.datasources.network.requests.AuthRequest
import motocitizen.datasources.preferences.Preferences
import motocitizen.utils.md5
import org.json.JSONException

object Auth {
    fun auth(login: String, password: String, callback: (ApiResponse) -> Unit) {
        Preferences.password = password
        AuthRequest(login, password.md5()) {
            authRequestCallback(it, login, callback)
        }
    }

    private fun authRequestCallback(response: ApiResponse, login: String, callback: (ApiResponse) -> Unit) {
        parseAuthResult(response, login)
        callback(response)
    }

    private fun parseAuthResult(response: ApiResponse, login: String) {
        User.isAuthorized = false
        try {
            val result = response.resultObject
            User.apply {
                id = Integer.parseInt(result.getString("id"))
                name = login
                role = Role.parse(result.getInt("r"))
                isAuthorized = true
            }
            Preferences.login = User.name
            Preferences.anonymous = false
        } catch (e: JSONException) {
            e.printStackTrace()
            Log.d("AUTH ERROR", response.toString())
        }
    }

    fun getPassHash(): String = Preferences.password.md5()

    fun logoff() = User.apply {
        name = ""
        role = Role.RO
        id = 0
        isAuthorized = false
    }
}