package motocitizen.datasources.network.requests

import motocitizen.content.accident.Accident
import motocitizen.datasources.network.ApiRequestWithAuth
import motocitizen.datasources.network.ApiResponse
import motocitizen.datasources.network.Methods
import motocitizen.datasources.preferences.Preferences


class CreateAccidentRequest(accident: Accident, callback: (ApiResponse) -> Unit, forStat: Boolean = false) : ApiRequestWithAuth(callback = callback) {
    override val method: String = Methods.CREATE
    init {
        params.apply {
            put("t", accident.type.code)
            put("a", accident.address)
            put("d", accident.description)
            put("dm", accident.medicine.code)
            put("y", accident.coordinates.latitude.toString())
            put("x", accident.coordinates.longitude.toString())
            if (forStat) put("s", "1")
            if (Preferences.isTester) put("test", "1")
        }
        call()
    }
}