package motocitizen.network.requests

import motocitizen.content.accident.Accident
import motocitizen.network.Methods
import motocitizen.network.RequestWithAuth
import motocitizen.user.User
import motocitizen.utils.getDbFormat

class CreateAccidentRequest(accident: Accident, callback: RequestResultCallback, forStat: Boolean = false) : RequestWithAuth(callback) {
    init {
        params.put("calledMethod", Methods.CREATE.code)
        params.put("owner_id", User.dirtyRead().id.toString())
        params.put("type", accident.type.code())
        params.put("address", accident.address)
        params.put("descr", accident.description)
        params.put("created", getDbFormat(accident.time))
        params.put("med", accident.medicine.code())
        params.put("lat", accident.coordinates.latitude.toString())
        params.put("lon", accident.coordinates.longitude.toString())
        if (forStat) params.put("stat", "1")
        call()
    }
}