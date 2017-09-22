package motocitizen.datasources.network.requests

import motocitizen.content.accident.Accident
import motocitizen.datasources.network.ApiRequestWithAuth
import motocitizen.datasources.network.ApiResponse
import motocitizen.datasources.network.Methods

class CreateAccidentRequest(accident: Accident, callback: (ApiResponse) -> Unit, forStat: Boolean = false) : ApiRequestWithAuth(callback) {
    init {
        with(params) {
            put("m", Methods.CREATE.code)
            put("t", accident.type.code)
            put("a", accident.address)
            put("d", accident.description)
            put("dm", accident.medicine.code)
            put("y", accident.coordinates.latitude.toString())
            put("x", accident.coordinates.longitude.toString())
            if (forStat) put("s", "1")
        }
        call()
    }
}