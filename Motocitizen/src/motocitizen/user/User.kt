package motocitizen.user

import android.util.Log
import motocitizen.network.CoreRequest
import motocitizen.network.requests.AuthRequest
import motocitizen.utils.Preferences
import org.json.JSONException
import org.json.JSONObject
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.experimental.and

object User {
    private var role = Role.RO
    var name = ""
        private set
    var id = 0
        private set
    var isAuthorized = false
        private set

    fun auth(login: String, password: String, callback: CoreRequest.RequestResultCallback) {
        Preferences.password = password
        AuthRequest(login, getPassHash(password), { response ->
            parseAuthResult(response, login)
            callback.call(response)
        })
    }

    private fun parseAuthResult(response: JSONObject, login: String) {
        isAuthorized = false
        try {
            val result = response.getJSONObject("r")
            id = Integer.parseInt(result.getString("id"))
            name = login
            role = Role.parse(result.getInt("r"))
            Preferences.login = name
            Preferences.anonim = false
            isAuthorized = true
        } catch (e: JSONException) {
            e.printStackTrace()
            Log.d("AUTH ERROR", response.toString())
        }

    }

    val passHash: String
        get() = getPassHash(Preferences.password)

    private fun getPassHash(pass: String): String {
        val sb = StringBuilder()
        try {
            val md = MessageDigest.getInstance("MD5")
            md.update(pass.toByteArray())
            val digest = md.digest()
            for (b in digest) {
                sb.append(String.format("%02x", b and 0xff.toByte()))
            }
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

        return sb.toString()
    }

    fun logoff() {
        name = ""
        role = Role.RO
        id = 0
        isAuthorized = false
    }

    val isModerator: Boolean
        get() = role.isModerator

    val isStandard: Boolean
        get() = role.isStandard

    val roleName: String
        get() = role.text
}
