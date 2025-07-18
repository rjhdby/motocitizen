package motocitizen.datasources.network

import motocitizen.utils.tryOr
import org.json.JSONObject

abstract class ApiRequest(private val method: String, val id: Int? = null, callback: (LegacyApiResponse) -> Unit = {}) : CoreRequest(callback) {
    override val url: String = "http://motodtp.info/mobile_api/"

    override fun call() {
        if (id != null) addParams("id" to id.toString())
        addParams("m" to method)
        super.call()
    }

    override fun response(string: String): LegacyApiResponse = LegacyApiResponse(
            tryOr(error) {
                JSONObject(string)
            })
}