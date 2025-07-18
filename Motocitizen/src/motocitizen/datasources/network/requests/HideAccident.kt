package motocitizen.datasources.network.requests

import motocitizen.datasources.network.ApiRequestWithAuth
import motocitizen.datasources.network.LegacyApiResponse
import motocitizen.datasources.network.Methods

class HideAccident(id: Int, callback: (LegacyApiResponse) -> Unit) : ApiRequestWithAuth(Methods.HIDE_ACCIDENT, id, callback = callback)