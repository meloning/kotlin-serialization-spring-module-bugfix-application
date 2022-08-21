package com.example.meloning.config.http

import org.slf4j.LoggerFactory
import org.springframework.http.client.ClientHttpResponse
import org.springframework.lang.NonNull
import org.springframework.web.client.ResponseErrorHandler
import java.io.BufferedReader
import java.io.InputStreamReader


class RestTemplateErrorHandler : ResponseErrorHandler {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun hasError(response: ClientHttpResponse): Boolean {
        return !response.statusCode.is2xxSuccessful
    }

    override fun handleError(response: ClientHttpResponse) {
        val error = getErrorAsString(response)
        val stringBuffer = StringBuffer()
        stringBuffer.append(NEW_LINE)
        stringBuffer.append("==============================[Response]==============================").append(NEW_LINE)
        stringBuffer.append("Headers : " + response.headers).append(NEW_LINE)
        stringBuffer.append("Response Status : " + response.rawStatusCode).append(NEW_LINE)
        stringBuffer.append("Response body : $error").append(NEW_LINE)

        logger.error(stringBuffer.toString())

        throw RuntimeException("RestTemplate Call is Not Successful")
    }

    private fun getErrorAsString(@NonNull response: ClientHttpResponse): String {
        BufferedReader(InputStreamReader(response.body)).use { br ->
            val stringBuffer = StringBuffer()
            br.lines().forEach { str: String? -> stringBuffer.append(str) }
            return stringBuffer.toString()
        }
    }

    companion object {
        const val NEW_LINE = "\n"
    }
}
