package motocitizen.datasources.network.requests

import motocitizen.datasources.network.ApiRequestWithAuth
import motocitizen.datasources.network.ApiResponse
import motocitizen.datasources.network.Methods

class LeaveRequest(id: Int, callback: (ApiResponse) -> Unit) : ApiRequestWithAuth(Methods.LEAVE, id, callback = callback)