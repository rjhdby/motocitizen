package motocitizen.datasources.network

import org.json.JSONObject

class Error(jsonObject: JSONObject) {
    val code: Int = if (jsonObject.has("c")) jsonObject.getInt("c") else 0
    val text: String = if (jsonObject.has("t")) jsonObject.getString("t") else ""
}