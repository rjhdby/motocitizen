package motocitizen.network

abstract class NewApiRequest(callback: RequestResultCallback? = null) : CoreRequest(callback) {
    override val url: String = "http://motodtp.info/mobile_api/"
}