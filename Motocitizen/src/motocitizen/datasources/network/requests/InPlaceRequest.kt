package motocitizen.datasources.network.requests

import motocitizen.datasources.network.ApiRequestWithAuth
import motocitizen.datasources.network.Methods

class InPlaceRequest(id: Int) : ApiRequestWithAuth(Methods.IN_PLACE, id)