package motocitizen.datasources.network.requests

import motocitizen.datasources.network.ApiRequestWithAuth
import motocitizen.datasources.network.LegacyApiResponse
import motocitizen.datasources.network.Methods

class AuthRequest(callback: (LegacyApiResponse) -> Unit) : ApiRequestWithAuth(Methods.AUTH, callback = callback)