package com.codependent.cdc.account.configuration

import com.codependent.cdc.account.mapper.ObjectMapper
import com.codependent.cdc.account.mapper.OrikaObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
class ObjectMapperConfig {

    @Bean
    fun objectMapper(): ObjectMapper {
        return OrikaObjectMapper(Optional.empty())
    }
}
