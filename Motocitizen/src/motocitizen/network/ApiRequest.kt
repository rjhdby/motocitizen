package motocitizen.network

abstract class ApiRequest(callback: RequestResultCallback? = null) : CoreRequest(callback) {
    override val url: String = "http://motodtp.info/mobile_api/"
}