package com.example.meloning.config.http

import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.BufferingClientHttpRequestFactory
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.http.converter.json.KotlinSerializationJsonHttpMessageConverter
import org.springframework.web.client.RestTemplate
import java.time.Duration

@Configuration
class RestTemplateConfig {

    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplateBuilder()
            // Resolved: HttpRetryException: cannot retry due to server authentication, in streaming mode
            // -> HttpComponentsClientHttpRequestFactory()
            // required: implementation('org.apache.httpcomponents:httpclient')
//            .requestFactory { BufferingClientHttpRequestFactory(HttpComponentsClientHttpRequestFactory()) }
            .requestFactory { BufferingClientHttpRequestFactory(SimpleClientHttpRequestFactory()) }
            .errorHandler(RestTemplateErrorHandler())
            .messageConverters(KotlinSerializationJsonHttpMessageConverter())
            .setConnectTimeout(Duration.ofMinutes(1))
            .additionalInterceptors(RestTemplateClientHttpRequestInterceptor())
            .build()
    }
}