package motocitizen.datasources.network.requests

import motocitizen.datasources.network.ApiRequestWithAuth
import motocitizen.datasources.network.ApiResponse
import motocitizen.datasources.network.Methods

class BanRequest(id: Int, callback: (ApiResponse) -> Unit) : ApiRequestWithAuth(Methods.BAN, id, callback = callback)