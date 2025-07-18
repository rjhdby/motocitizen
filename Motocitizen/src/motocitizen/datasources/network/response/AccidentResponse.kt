package motocitizen.datasources.network.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.android.gms.maps.model.LatLng
import motocitizen.content.accident.Accident
import motocitizen.dictionary.AccidentStatus
import motocitizen.dictionary.Medicine
import motocitizen.dictionary.Type
import motocitizen.geo.geocoder.AccidentLocation
import java.time.Instant
import java.util.Date

data class AccidentResponse(
    @JsonProperty("id") val id: Int,
    @JsonProperty("ut") val time: Long,
    @JsonProperty("a") val address: String,
    @JsonProperty("d") val description: String,
    @JsonProperty("s") val status: AccidentStatus,
    @JsonProperty("o") val owner: Int,
    @JsonProperty("x") val lon: Double,
    @JsonProperty("y") val lat: Double,
    @JsonProperty("t") val type: Type,
    @JsonProperty("m") val medicine: Medicine,
    @JsonProperty("lm") val lm: Int,
    @JsonProperty("mc") val messagesCount: Int,
): ResponsePayload {
    fun toAccident() = Accident(
        id = id,
        type = type,
        medicine = medicine,
        time = Date.from(Instant.ofEpochSecond(time)),
        location = AccidentLocation(
            address = address,
            coordinates = LatLng(lat, lon),
        ),
        owner = owner,
        status = status,
        description = description,
        messagesCount = messagesCount,
    )
}
