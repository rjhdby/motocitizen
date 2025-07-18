package motocitizen.datasources.network.requests

import motocitizen.content.accident.Accident
import motocitizen.datasources.network.ApiRequestWithAuth
import motocitizen.datasources.network.LegacyApiResponse
import motocitizen.datasources.network.Methods
import motocitizen.datasources.preferences.Preferences


class CreateAccidentRequest(accident: Accident, callback: (LegacyApiResponse) -> Unit, forStat: Boolean = false) : ApiRequestWithAuth(Methods.CREATE, callback = callback) {
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