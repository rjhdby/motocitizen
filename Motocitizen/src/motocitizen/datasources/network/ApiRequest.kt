package motocitizen.datasources.network

import org.json.JSONException
import org.json.JSONObject

abstract class ApiRequest(callback: (ApiResponse) -> Unit = {}) : CoreRequest(callback) {
    override val url: String = "http://motodtp.info/mobile_api/"

    override fun response(string: String): ApiResponse = ApiResponse(
            try {
                JSONObject(string)
            } catch (e: JSONException) {
                error
            })
}