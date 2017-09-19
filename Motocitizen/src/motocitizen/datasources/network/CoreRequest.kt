package motocitizen.datasources.network

import motocitizen.main.BuildConfig
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

abstract class CoreRequest(val callback: (JSONObject) -> Unit = {}) {
    var params: HashMap<String, String> = HashMap()
    abstract val url: String
    private val error = JSONObject("""{"e":{"c":0,"t":"server error"}}""")
    private val logLevel = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    private val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(logLevel))
            .build()

    private fun buildRequest() = Request.Builder()
            .post(makePost(params))
            .url(url)
            .build()

    protected fun call() {
        client.newCall(buildRequest()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(response("HTTP RESPONSE ERROR " + e.toString()))
            }

            @Throws(java.io.IOException::class)
            override fun onResponse(call: Call, response: Response) {
                callback(response(response.body().string()))
            }
        })
    }

    private fun response(string: String): JSONObject {
        return try {
            JSONObject(string)
        } catch (e: JSONException) {
            error
        }
    }

    private fun makePost(post: Map<String, String>): RequestBody {
        val body = FormBody.Builder()
        post.forEach { (k, v) -> body.add(k, v) }
        return body.build()
    }
}