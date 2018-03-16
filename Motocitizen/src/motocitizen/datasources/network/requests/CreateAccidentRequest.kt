package motocitizen.datasources.network.requests

import motocitizen.content.accident.Accident
import motocitizen.datasources.network.ApiRequestWithAuth
import motocitizen.datasources.network.ApiResponse
import motocitizen.datasources.network.Methods
import motocitizen.datasources.preferences.Preferences


class CreateAccidentRequest(accident: Accident, callback: (ApiResponse) -> Unit, forStat: Boolean = false) : ApiRequestWithAuth(callback = callback) {
    override val method: String = Methods.CREATE

    init {
        addParams(
                "t" to accident.type.code,
                "a" to accident.address,
                "d" to accident.description,
                "dm" to accident.medicine.code,
                "y" to accident.coordinates.latitude.toString(),
                "x" to accident.coordinates.longitude.toString()
                 )
        if (forStat) addParams("s" to "1")
        if (Preferences.isTester) addParams("test" to "1")
    }
}