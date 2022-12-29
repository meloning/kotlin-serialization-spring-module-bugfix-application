package com.example.meloning.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MockLabRequestDto<T>(
    val payload: T,
    @SerialName("tester_code")
    val testerCode: String
)
