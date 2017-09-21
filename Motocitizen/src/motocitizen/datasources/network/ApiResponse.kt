package motocitizen.datasources.network

import org.json.JSONArray
import org.json.JSONObject

class ApiResponse(jsonObject: JSONObject) {
    val resultArray: JSONArray = if (jsonObject.get("r") is JSONArray) jsonObject.getJSONArray("r") else JSONArray()
    val resultObject: JSONObject = if (jsonObject.get("r") is JSONObject) jsonObject.getJSONObject("r") else JSONObject()
    val error = Error(jsonObject.getJSONObject("e"))
    fun hasError(): Boolean = error.code != 0
}