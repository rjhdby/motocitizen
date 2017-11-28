package motocitizen.datasources.network

import motocitizen.utils.getIntOr
import motocitizen.utils.getStringOr
import org.json.JSONObject

class Error(json: JSONObject) {
    val code: Int = json.getIntOr("c", 0)
    val text: String = json.getStringOr("t", "")
}