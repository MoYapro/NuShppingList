package de.moyapro.nushppinglist.serialization

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule

object ConfiguredObjectMapper : ObjectMapper() {
    private val instance = ObjectMapper().registerModule(KotlinModule())
    operator fun invoke(): ObjectMapper {
        return instance
    }
}


