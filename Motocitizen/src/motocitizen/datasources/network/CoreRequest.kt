package motocitizen.datasources.network

import motocitizen.main.BuildConfig
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import java.io.IOException

abstract class CoreRequest(val callback: (ApiResponse) -> Unit = {}) {
    var params: HashMap<String, String> = HashMap()
    abstract val url: String
    val error = JSONObject("""{"r":{},"e":{"c":0,"t":"server error"}}""")
    private val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(logLevel()))
            .build()

    private fun buildRequest() = Request.Builder()
            .post(makePost(params))
            .url(url)
            .build()

    protected open fun call() = client.newCall(buildRequest()).enqueue(enqueueCallback())

    private fun enqueueCallback(): Callback = object : Callback {
        override fun onFailure(call: Call, e: IOException) = callback(ApiResponse(error))

        @Throws(IOException::class)
        override fun onResponse(call: Call, response: Response) = callback(response(response.body().string()))
    }

    abstract fun response(string: String): ApiResponse

    private fun makePost(post: Map<String, String>): RequestBody {
        val body = FormBody.Builder()
        post.forEach { (k, v) -> body.add(k, v) }
        return body.build()
    }

    private fun logLevel() = when {
        BuildConfig.DEBUG -> HttpLoggingInterceptor.Level.BODY
        else              -> HttpLoggingInterceptor.Level.NONE
    }
}