package motocitizen.datasources.network.requests

import motocitizen.datasources.network.ApiRequestWithAuth
import motocitizen.datasources.network.ApiResponse
import motocitizen.datasources.network.Methods

class HideAccident(id: Int, callback: (ApiResponse) -> Unit) : ApiRequestWithAuth(Methods.HIDE_ACCIDENT, id, callback = callback)