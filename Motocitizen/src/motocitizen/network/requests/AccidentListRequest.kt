package motocitizen.network.requests

import motocitizen.network.Methods
import motocitizen.network.NewApiRequest
import motocitizen.user.User
import motocitizen.utils.Preferences

class AccidentListRequest(callback: RequestResultCallback) : NewApiRequest(callback) {
    //    init {
//        if (User.dirtyRead().name != "") {
//            params.put("user", User.dirtyRead().name)
//        }
//        params.put("age", (if (Preferences.dirtyRead() == null) 24 else Preferences.dirtyRead()!!.hoursAgo).toString())
//        params.put("m", Methods.GET_LIST.code)
//        call()
//    }
    init {
        if (User.dirtyRead().name != "") {
            params.put("u", User.dirtyRead().name)
        }
        params.put("a", (if (Preferences.dirtyRead() == null) 24 else Preferences.dirtyRead()!!.hoursAgo).toString())
        params.put("m", Methods.GET_LIST.code)
        call()
    }
}