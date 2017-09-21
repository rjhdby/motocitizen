package motocitizen.datasources.network.requests

import motocitizen.content.accident.Accident
import motocitizen.datasources.network.ApiRequestWithAuth
import motocitizen.datasources.network.ApiResponse
import motocitizen.datasources.network.Methods

class CreateAccidentRequest(accident: Accident, callback: (ApiResponse) -> Unit, forStat: Boolean = false) : ApiRequestWithAuth(callback) {
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