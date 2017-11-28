package motocitizen.datasources.network.requests

import motocitizen.content.accident.Accident
import motocitizen.datasources.network.ApiRequestWithAuth
import motocitizen.datasources.network.ApiResponse
import motocitizen.datasources.network.Methods

class LeaveRequest(accident: Accident, callback: (ApiResponse) -> Unit) : ApiRequestWithAuth(callback = callback) {
    init {
        with(params) {
            put("id", accident.id.toString())
            put("m", Methods.LEAVE.code)
        }
        call()
    }
}