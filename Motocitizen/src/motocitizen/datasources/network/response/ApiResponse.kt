package motocitizen.datasources.network.response

import com.fasterxml.jackson.annotation.JsonProperty

data class ApiResponse<T: ResponsePayload>(
    @JsonProperty("r") val result: T,
    @JsonProperty("e") val error: Error,
) {
    data class Error(
        @JsonProperty("c") val code: Int = 0,
        @JsonProperty("t") val text: String = "",
    )
}