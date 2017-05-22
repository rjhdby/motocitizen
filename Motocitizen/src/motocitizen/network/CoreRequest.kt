package motocitizen.network

import motocitizen.main.BuildConfig
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

abstract class CoreRequest(val callback: RequestResultCallback? = null) {
    var params: HashMap<String, String> = HashMap()
    abstract val url: String
    private val error = JSONObject("{\"error\":\"server error\"}")
    private val logLevel = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    private val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(logLevel))
            .build()

    private fun buildRequest() = Request.Builder()
            .post(makePost(params))
            .url(url)
            .build()!!

    protected fun call() {
        client.newCall(buildRequest()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback?.call(response("HTTP RESPONSE ERROR " + e.toString()))
            }

            @Throws(java.io.IOException::class)
            override fun onResponse(call: Call, response: Response) {
                callback?.call(response(response.body().string()))
            }
        })
    }

    private fun response(string: String): JSONObject {
        try {
            return JSONObject(string)
        } catch (e: JSONException) {
            return error
        }
    }

    private fun makePost(post: Map<String, String>): RequestBody {
        val body = FormBody.Builder()
        post.forEach { (k, v) -> body.add(k, v) }
        return body.build()
    }

    @FunctionalInterface
    interface RequestResultCallback {
        fun call(response: JSONObject)
    }
}