package motocitizen.network2.requests

import motocitizen.dictionary.Content
import motocitizen.network.Methods
import motocitizen.network2.RequestWithAuth

class BanRequest(id: Int, callback: RequestResultCallback) : RequestWithAuth(callback) {
    init {
        val user = Content.getInstance()[id]!!.owner.id
        params.put("id", id.toString())
        params.put("user_id", user.toString())
        params.put("calledMethod", Methods.BAN.toCode())
        call()
    }
}