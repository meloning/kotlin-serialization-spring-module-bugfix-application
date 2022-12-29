package com.example.meloning.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MockLabCreateTesterDto(
    @SerialName("mocklab_tester")
    val mockLabTester: String,
    @SerialName("tester_info")
    val testerInfo: String
)
