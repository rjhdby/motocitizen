package motocitizen.datasources.network

import motocitizen.utils.getArrayOrEmpty
import motocitizen.utils.getObjectOrEmpty
import org.json.JSONObject

class ApiResponse(json: JSONObject) {
    val resultArray = json.getArrayOrEmpty("r")
    val resultObject = json.getObjectOrEmpty("r")
    val error = Error(json.getJSONObject("e"))
    fun hasError(): Boolean = error.code != 0
}