package com.example.meloning.restTemplate.config.http

import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.BufferingClientHttpRequestFactory
import org.springframework.http.client.SimpleClientHttpRequestFactory
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
//                .requestFactory(BufferingClientHttpRequestFactory(SimpleClientHttpRequestFactory()))
            .errorHandler(RestTemplateErrorHandler())
            .setConnectTimeout(Duration.ofMinutes(1))
            .additionalInterceptors(RestTemplateClientHttpRequestInterceptor())
            .build()
    }
}