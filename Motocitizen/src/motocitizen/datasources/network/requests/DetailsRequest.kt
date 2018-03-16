package motocitizen.datasources.network.requests

import motocitizen.datasources.network.ApiRequest
import motocitizen.datasources.network.ApiResponse
import motocitizen.datasources.network.Methods

class DetailsRequest(id: Int, callback: (ApiResponse) -> Unit) : ApiRequest(Methods.DETAILS, id, callback)