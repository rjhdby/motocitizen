package motocitizen.datasources.network.response

import com.fasterxml.jackson.annotation.JsonProperty
import motocitizen.content.volunteer.Volunteer

data class AccidentListResponse(
    @JsonProperty("l") val accidents: List<AccidentResponse> = emptyList(),
    @JsonProperty("u") val volunteers: Map<Int, String> = emptyMap(),
) : ResponsePayload {
    fun getVolunteers() = volunteers.map { Volunteer(it.key, it.value) }
}
