package motocitizen.network.requests

import motocitizen.content.accident.Accident
import motocitizen.network.Methods
import motocitizen.network.NewApiRequestWithAuth
import motocitizen.network.RequestWithAuth
import motocitizen.user.User
import motocitizen.utils.getDbFormat

class CreateAccidentRequest(accident: Accident, callback: RequestResultCallback, forStat: Boolean = false) : NewApiRequestWithAuth(callback) {
    init {
        params.put("m", Methods.CREATE.code)
        params.put("t", accident.type.code)
        params.put("a", accident.address)
        params.put("d", accident.description)
        params.put("dm", accident.medicine.code)
        params.put("y", accident.coordinates.latitude.toString())
        params.put("x", accident.coordinates.longitude.toString())
        if (forStat) params.put("s", "1")
        call()
    }
}