package motocitizen.datasources.network.requests

import motocitizen.content.accident.Accident
import motocitizen.datasources.network.ApiRequestWithAuth
import motocitizen.datasources.network.Methods

class InPlaceRequest(accident: Accident) : ApiRequestWithAuth() {
    init {
        with(params) {
            put("id", accident.id.toString())
            put("m", Methods.IN_PLACE.code)
        }
        call()
    }
}