package motocitizen.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef

inline fun <reified T> ObjectMapper.readValue(json: String): T {
    return this.readValue(json, jacksonTypeRef<T>())
}