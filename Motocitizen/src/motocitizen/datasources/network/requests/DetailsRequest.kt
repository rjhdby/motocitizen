package motocitizen.datasources.network.requests

import motocitizen.datasources.network.ApiRequest
import motocitizen.datasources.network.LegacyApiResponse
import motocitizen.datasources.network.Methods

class DetailsRequest(id: Int, callback: (LegacyApiResponse) -> Unit) : ApiRequest(Methods.DETAILS, id, callback)