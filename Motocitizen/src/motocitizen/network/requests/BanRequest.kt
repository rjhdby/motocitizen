package motocitizen.network.requests

import motocitizen.content.NewContent
import motocitizen.network.Methods
import motocitizen.network.RequestWithAuth

class BanRequest(id: Int, callback: RequestResultCallback) : RequestWithAuth(callback) {
    init {
//        val user = Content.getInstance()[id]!!.owner.id
        val user = NewContent.accidents[id]!!.owner
        params.put("id", id.toString())
        params.put("user_id", user.toString())
        params.put("calledMethod", Methods.BAN.code)
        call()
    }
}