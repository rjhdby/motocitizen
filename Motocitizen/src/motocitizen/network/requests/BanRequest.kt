package motocitizen.network.requests

import motocitizen.content.Content
import motocitizen.network.Methods
import motocitizen.network.RequestWithAuth

class BanRequest(id: Int, callback: RequestResultCallback) : RequestWithAuth(callback) {
    init {
//        val user = ContentLegacy.getInstance()[id]!!.owner.id
        val user = Content.accidents[id]!!.owner
        params.put("id", id.toString())
        params.put("user_id", user.toString())
        params.put("calledMethod", Methods.BAN.code)
        call()
    }
}