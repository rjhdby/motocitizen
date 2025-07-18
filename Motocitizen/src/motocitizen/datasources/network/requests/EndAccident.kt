package motocitizen.datasources.network.requests

import motocitizen.datasources.network.ApiRequestWithAuth
import motocitizen.datasources.network.LegacyApiResponse
import motocitizen.datasources.network.Methods

class EndAccident(id: Int, callback: (LegacyApiResponse) -> Unit = {}) : ApiRequestWithAuth(Methods.END_ACCIDENT, id, callback = callback)