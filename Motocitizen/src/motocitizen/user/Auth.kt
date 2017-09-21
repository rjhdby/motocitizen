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
        AuthRequest(login, makePassHash(password), { response ->
            authRequestCallback(response, login, callback)
        })
    }

    private fun authRequestCallback(response: ApiResponse, login: String, callback: (ApiResponse) -> Unit) {
        parseAuthResult(response, login)
        callback(response)
    }

    private fun parseAuthResult(response: ApiResponse, login: String) {
        User.isAuthorized = false
        try {
            val result = response.resultObject
            User.id = Integer.parseInt(result.getString("id"))
            User.name = login
            User.role = Role.parse(result.getInt("r"))
            Preferences.login = User.name
            Preferences.anonymous = false
            User.isAuthorized = true
        } catch (e: JSONException) {
            e.printStackTrace()
            Log.d("AUTH ERROR", response.toString())
        }
    }

    fun getPassHash(): String = Auth.makePassHash(Preferences.password)

    private fun makePassHash(pass: String): String = md5(pass)

    fun logoff() {
        User.name = ""
        User.role = Role.RO
        User.id = 0
        User.isAuthorized = false
    }
}