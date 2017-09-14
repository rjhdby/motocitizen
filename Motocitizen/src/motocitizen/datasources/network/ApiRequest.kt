package motocitizen.datasources.network

import org.json.JSONObject

abstract class ApiRequest(callback: (JSONObject) -> Unit) : CoreRequest(callback) {
    override val url: String = "http://motodtp.info/mobile_api/"
}