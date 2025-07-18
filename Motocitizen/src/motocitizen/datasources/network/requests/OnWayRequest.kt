package motocitizen.datasources.network.requests

import motocitizen.datasources.network.ApiRequestWithAuth
import motocitizen.datasources.network.LegacyApiResponse
import motocitizen.datasources.network.Methods

class OnWayRequest(id: Int, callback: (LegacyApiResponse) -> Unit) : ApiRequestWithAuth(Methods.ON_WAY, id, callback = callback)