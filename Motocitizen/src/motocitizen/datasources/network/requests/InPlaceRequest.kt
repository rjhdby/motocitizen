package motocitizen.datasources.network.requests

import motocitizen.content.accident.Accident
import motocitizen.datasources.network.ApiRequestWithAuth
import motocitizen.datasources.network.Methods

class InPlaceRequest(accident: Accident) : ApiRequestWithAuth() {
    override val method = Methods.IN_PLACE

    init {
        params["id"] = accident.id.toString()
        call()
    }
}