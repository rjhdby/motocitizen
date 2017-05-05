package motocitizen.network2

import okhttp3.OkHttpClient
import org.json.JSONObject


abstract class ApiRequest(var params: HashMap<String, String>, val callback: RequestResultCallback? = null) {
    private val url = "http://motodtp.info/mobile/main_mc_acc_json.php"

    fun sync(): JSONObject {
        val text = OkHttpClient()
                .newCall(buildRequest())
                .execute()
                .body()
                .string()
        android.util.Log.w("HTTP RESPONSE", text)
        var response = JSONObject()
        try {
            response = JSONObject(text)
        } catch (e: org.json.JSONException) {
            e.printStackTrace()
        }
        return response
    }

    private fun buildRequest() = okhttp3.Request.Builder()
            .post(makePost(params))
            .url(url)
            .build()!!

    protected fun async() {
        if (callback == null) return
        okhttp3.OkHttpClient().newCall(buildRequest()).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) = e.printStackTrace()

            @Throws(java.io.IOException::class)
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val text = response.body().string()
                android.util.Log.w("HTTP RESPONSE", text)
                try {
                    callback.call(org.json.JSONObject(text))
                } catch (e: org.json.JSONException) {
                    e.printStackTrace()
                    callback.call(org.json.JSONObject())
                }

            }
        })
    }

    private fun makePost(post: Map<String, String>): okhttp3.RequestBody {
        val debug = StringBuilder()
        debug.append(url).append("?")
        val body = okhttp3.FormBody.Builder()
        for (key in post.keys) {
            body.add(key, post[key])
            debug.append(key).append("=").append(post[key]).append("&")
        }
        android.util.Log.w("HTTP REQUEST", debug.toString())
        return body.build()
    }

    interface RequestResultCallback {
        fun call(response: org.json.JSONObject)
    }
}