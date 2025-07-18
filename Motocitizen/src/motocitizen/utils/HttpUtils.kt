package motocitizen.utils

import android.util.Log
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import motocitizen.datasources.network.Methods
import motocitizen.datasources.network.response.AccidentListResponse
import motocitizen.datasources.network.response.AccidentResponse
import motocitizen.datasources.network.response.ApiResponse
import motocitizen.datasources.network.response.ResponsePayload
import motocitizen.datasources.preferences.Preferences
import motocitizen.user.User
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

private val API_URL: HttpUrl = "http://motodtp.info/mobile_api/".toHttpUrl()
val objectMapper: ObjectMapper = ObjectMapper().registerModule(kotlinModule())
val httpClient = OkHttpClient()

fun buildGet(params: Map<String, String> = emptyMap()) = API_URL.newBuilder().apply {
    params.forEach { addQueryParameter(it.key, it.value) }
    if (Preferences.isTester) addQueryParameter("test", "1")
    if (User.name != "") addQueryParameter("u", User.name)
}
    .build()
    .let { Request.Builder().url(it).get().build() }

inline fun <reified T : ResponsePayload> getMethod() = when (T::class) {
    AccidentListResponse::class -> Methods.LIST
    AccidentResponse::class -> Methods.ACCIDENT
    else -> error("Not all methods are implemented")
}

suspend inline fun <reified T : ResponsePayload> OkHttpClient.getRequest(
    vararg params: Pair<String, Any>,
    crossinline callback: (T) -> Unit,
) = withContext(Dispatchers.IO) {
    val method = getMethod<T>()
    val request = buildGet(params.associate { it.first to it.second.toString() } + mapOf("m" to method))
    httpClient.newCall(request).execute().use {
        if (!it.isSuccessful) {
            Log.e("$method request", it.message)
            return@withContext
        }
        val result = objectMapper.readValue<ApiResponse<T>>(it.body!!.string())
        callback(result.result)
    }
}