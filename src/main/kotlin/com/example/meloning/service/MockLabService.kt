package com.example.meloning.service

import com.example.meloning.dto.MockLabCreateTesterDto
import com.example.meloning.dto.MockLabRequestDto
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class MockLabService(
    private val restTemplate: RestTemplate
) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun createMockLabTester() {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON

        val body = MockLabRequestDto(
//            payload = mapOf(
//                "mocklab_tester" to "meloning",
//                "tester_info" to "contributor"
//            ),
            payload = MockLabCreateTesterDto("meloning", "contributor"),
            testerCode = "CONTRIBUTE_MELONING"
        )

        val httpEntity = HttpEntity(
            body,
            httpHeaders
        )

        val result = restTemplate.exchange(
            "$MOCKLAB_TEST_URL/json-test",
            HttpMethod.POST,
            httpEntity,
            String::class.java
        )

        logger.info(result.toString())
    }

    companion object {
        const val MOCKLAB_TEST_URL = "https://w9v21.mocklab.io"
    }
}
