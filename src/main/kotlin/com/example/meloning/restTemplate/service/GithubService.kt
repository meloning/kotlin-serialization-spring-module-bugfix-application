package com.example.meloning.restTemplate.service

import com.example.meloning.restTemplate.dto.GithubUserInfoResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.reactive.function.client.WebClient

@Service
class GithubService(
    private val restTemplate: RestTemplate,
    private val webClient: WebClient
) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun getUserRestTemplate(githubId: String) {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON

        val httpEntity = HttpEntity(null, httpHeaders)

        val responseEntity = restTemplate.exchange(
            "https://api.github.com/users/$githubId",
            HttpMethod.GET,
            httpEntity,
            GithubUserInfoResponse::class.java
        )

        logger.info(responseEntity.toString())
    }

    fun getUserWebClient(githubId: String) {
        val result = webClient
            .get()
            .uri("https://api.github.com/users/$githubId")
            .retrieve()
            .bodyToMono(GithubUserInfoResponse::class.java)
            .block()

        logger.info(result.toString())
    }
}