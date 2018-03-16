package motocitizen.datasources.network.requests

import motocitizen.datasources.network.ApiRequestWithAuth
import motocitizen.datasources.network.ApiResponse
import motocitizen.datasources.network.Methods

class AuthRequest(callback: (ApiResponse) -> Unit) : ApiRequestWithAuth(Methods.AUTH, callback = callback)