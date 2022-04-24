package de.moyapro.nushppinglist.serialization

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import de.moyapro.nushppinglist.constants.CONSTANTS.ENABLED

object ConfiguredObjectMapper : ObjectMapper() {
    private val instance = ObjectMapper()
        .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
        .registerModule(KotlinModule.Builder()
            .withReflectionCacheSize(512)
            .configure(KotlinFeature.NullToEmptyCollection, ENABLED)
            .configure(KotlinFeature.NullToEmptyMap, ENABLED)
            .configure(KotlinFeature.NullIsSameAsDefault, ENABLED)
            .configure(KotlinFeature.SingletonSupport, ENABLED)
            .configure(KotlinFeature.StrictNullChecks, ENABLED)
            .build())

    operator fun invoke(): ObjectMapper {
        return instance
    }
}


