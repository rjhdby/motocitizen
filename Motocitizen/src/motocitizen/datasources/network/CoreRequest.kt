package motocitizen.datasources.network

import com.google.firebase.BuildConfig
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import java.io.IOException

abstract class CoreRequest(val callback: (LegacyApiResponse) -> Unit = {}) {
    abstract val url: String
    private val params: HashMap<String, String> = hashMapOf()

    val error = JSONObject("""{"r":{},"e":{"c":0,"t":"server error"}}""")
    private val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(logLevel()))
            .build()

    private fun buildRequest() = Request.Builder()
            .post(makePost(params))
            .url(url)
            .build()

    open fun call() = client.newCall(buildRequest()).enqueue(enqueueCallback())

    private fun enqueueCallback(): Callback = object : Callback {
        override fun onFailure(call: Call, e: IOException) = callback(LegacyApiResponse(error))

        @Throws(IOException::class)
        override fun onResponse(call: Call, response: Response) = callback(response(response.body?.string()?:"")) //todo
    }

    abstract fun response(string: String): LegacyApiResponse

    private fun makePost(post: Map<String, String>): RequestBody {
        val body = FormBody.Builder()
        post.forEach { (k, v) -> body.add(k, v) }
        return body.build()
    }

    private fun logLevel() = when {
        BuildConfig.DEBUG -> HttpLoggingInterceptor.Level.BODY
        else              -> HttpLoggingInterceptor.Level.NONE
    }

    protected fun addParams(vararg fields: Pair<String, String>) {
        fields.forEach { params[it.first] = it.second }
    }
}