package com.example.meloning.config.http

import org.slf4j.LoggerFactory
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.lang.NonNull
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

class RestTemplateClientHttpRequestInterceptor : ClientHttpRequestInterceptor {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution
    ): ClientHttpResponse {
        loggingRequest(request, body)
        val response = execution.execute(request, body)
        loggingResponse(response)
        return response
    }

    private fun loggingRequest(request: HttpRequest, body: ByteArray) {
        val stringBuffer = StringBuffer()
        stringBuffer.append(NEW_LINE)
        stringBuffer.append("==============================[Request]==============================").append(NEW_LINE)
        stringBuffer.append("Headers : " + request.headers).append(NEW_LINE)
        stringBuffer.append("Request Method : " + request.method).append(NEW_LINE)
        stringBuffer.append("Request URI : " + request.uri).append(NEW_LINE)
        stringBuffer.append(
            "Request body : " +
                if (body.isEmpty()) null
                else String(body, StandardCharsets.UTF_8)
        ).append(NEW_LINE)
        logger.info(stringBuffer.toString())
    }

    private fun loggingResponse(response: ClientHttpResponse) {
        val body = getBody(response)
        val stringBuffer = StringBuffer()
        stringBuffer.append(NEW_LINE)
        stringBuffer.append("==============================[Response]==============================").append(NEW_LINE)
        stringBuffer.append("Headers : " + response.headers).append(NEW_LINE)
        stringBuffer.append("Response Status : " + response.rawStatusCode).append(NEW_LINE)
        stringBuffer.append("Response body : $body").append(NEW_LINE)
        logger.info(stringBuffer.toString())
    }

    @Suppress("TooGenericExceptionThrown", "SwallowedException")
    private fun getBody(@NonNull response: ClientHttpResponse): String {
        try {
            BufferedReader(InputStreamReader(response.body)).use { br ->
                val stringBuffer = StringBuffer()
                br.lines().forEach { str: String? -> stringBuffer.append(str) }
                return stringBuffer.toString()
            }
        } catch (e: IOException) {
            throw RuntimeException(e.localizedMessage)
        }
    }

    companion object {
        const val NEW_LINE = "\n"
    }
}
