package motocitizen.datasources.network.requests

import motocitizen.content.accident.Accident
import motocitizen.datasources.network.ApiRequestWithAuth
import motocitizen.datasources.network.Methods

class InPlaceRequest(accident: Accident) : ApiRequestWithAuth() {
    init {
        params.put("id", accident.id.toString())
        params.put("m", Methods.IN_PLACE.code)
        call()
    }
}