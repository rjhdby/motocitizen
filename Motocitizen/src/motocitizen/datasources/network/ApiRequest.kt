package motocitizen.datasources.network

import org.json.JSONException
import org.json.JSONObject

abstract class ApiRequest(private val method: String, val id: Int? = null, callback: (ApiResponse) -> Unit = {}) : CoreRequest(callback) {
    override val url: String = "http://motodtp.info/mobile_api/"

    override fun call() {
        if (id != null) addParams("id" to id.toString())
        addParams("m" to method)
        super.call()
    }

    override fun response(string: String): ApiResponse = ApiResponse(
            try {
                JSONObject(string)
            } catch (e: JSONException) {
                error
            })
}