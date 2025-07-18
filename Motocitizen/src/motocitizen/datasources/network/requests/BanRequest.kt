package motocitizen.datasources.network.requests

import motocitizen.datasources.network.ApiRequestWithAuth
import motocitizen.datasources.network.LegacyApiResponse
import motocitizen.datasources.network.Methods

class BanRequest(id: Int, callback: (LegacyApiResponse) -> Unit) : ApiRequestWithAuth(Methods.BAN, id, callback = callback)